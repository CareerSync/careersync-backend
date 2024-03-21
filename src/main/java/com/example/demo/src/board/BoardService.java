package com.example.demo.src.board;

import com.example.demo.common.entity.BaseEntity.State;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.file.FileHandler;
import com.example.demo.common.payment.IamportClientInitializer;
import com.example.demo.src.admin.model.PostBoardLogTimeReq;
import com.example.demo.src.board.entity.Board;
import com.example.demo.src.board.entity.BoardImage;
import com.example.demo.src.board.model.*;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.common.entity.BaseEntity.State.ACTIVE;
import static com.example.demo.common.response.BaseResponseStatus.*;
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardImageRepository boardImageRepository;
    private final AuditReader auditReader;
    private final FileHandler fileHandler;

    // POST
    public PostBoardRes createBoard(Long userId, PostBoardReq req, List<MultipartFile> images) throws Exception {

        User user = userRepository.findByIdAndState(userId, ACTIVE).
                orElseThrow(() -> new BaseException(INVALID_USER));

        // 이미지 업로드 했는지 확인
        if(images.isEmpty()) {
            throw new BaseException(IMAGE_NOT_EXISTS_ERROR);
        }

        // 업로드 한 10장 넘어간다면 예외처리
        if (images.size() > 10) {
            throw new BaseException(IMAGE_OVERFLOW_ERROR);
        }

        Board saveBoard = boardRepository.save(req.toEntity(user));
        List<BoardImage> imageList = fileHandler.parseFileInfo(images);

        // 이미지 파일 저장 후, List<BoardImage>로 변환해줄 FileHandler 사용
        // 파일이 존재할 때에만 처리
        if(!imageList.isEmpty()) {
            for(BoardImage image : imageList) {
                // 파일을 DB에 저장
                saveBoard.addImage(boardImageRepository.save(image));
            }
        }

        return new PostBoardRes(saveBoard.getId(), saveBoard.getContent());
    }
    // GET
    @Transactional(readOnly = true)
    public List<GetBoardRes> getBoards() {
        List<GetBoardRes> getBoardsResList = boardRepository.findAllByState(ACTIVE).stream()
                .map(GetBoardRes::new)
                .collect(Collectors.toList());

        return getBoardsResList;
    }

    @Transactional(readOnly = true)
    public List<GetBoardRes> getBoardsByUserId(Long userId) {
        User user = userRepository.findByIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));

        List<GetBoardRes> getBoardsResList = boardRepository.findAllByUserAndState(user, ACTIVE).stream()
                .map(GetBoardRes::new)
                .collect(Collectors.toList());

        return getBoardsResList;
    }

    @Transactional(readOnly = true)
    public List<GetBoardRes> getBoardsByUserIdWithPaging(Long userId, Integer pageIndex, Integer size) {
        User user = userRepository.findByIdAndState(userId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));

        Pageable pageable = PageRequest.of(pageIndex, size);
        List<GetBoardRes> getBoardsResList = boardRepository.findAllByUserAndState(user, ACTIVE, pageable).stream()
                .map(GetBoardRes::new)
                .collect(Collectors.toList());

        return getBoardsResList;
    }

    @Transactional(readOnly = true)
    public GetBoardRes getBoard(Long boardId) {
        Board board = boardRepository.findByIdAndState(boardId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_BOARD));
        return new GetBoardRes(board);
    }

    @Transactional(readOnly = true)
    public List<GetBoardLogRes> getBoardHistoryByRevType(String revType) {

        if (!revType.equals("INSERT") && !revType.equals("UPDATE") && !revType.equals("DELETE")) {
            throw new BaseException(REVTYPE_ERROR);
        }

        List<Object> revs = getRevs();

        List<GetBoardLogRes> boardLogs = new ArrayList<>();
        revs.forEach(revision -> {
            Object[] revisionArray = (Object[]) revision;
            com.example.demo.src.revision.entity.Revision revObject = (com.example.demo.src.revision.entity.Revision) revisionArray[1];
            getBoardLogResByType(boardLogs, revObject.getId(), revType);
        });

        return boardLogs;
    }

    @Transactional(readOnly = true)
    public List<GetBoardLogRes> getBoardHistory() {

        List<Object> revs = getRevs();

        List<GetBoardLogRes> boardLogs = new ArrayList<>();

        revs.forEach(revision -> {
            Object[] revisionArray = (Object[]) revision;
            com.example.demo.src.revision.entity.Revision revObject = (com.example.demo.src.revision.entity.Revision) revisionArray[1];
            getBoardLogRes(boardLogs, revObject.getId());
        });

        return boardLogs;
    }

    @Transactional(readOnly = true)
    public List<GetBoardLogRes> getBoardHistoryByTime(PostBoardLogTimeReq req) {

        LocalDateTime startTime = req.getStartTime();
        LocalDateTime endTime = req.getEndTime();

        List<Object> revs = getRevs();

        List<GetBoardLogRes> boardLogs = new ArrayList<>();

        revs.forEach(revision -> {
            Object[] revisionArray = (Object[]) revision;
            com.example.demo.src.revision.entity.Revision revObject = (com.example.demo.src.revision.entity.Revision) revisionArray[1];
            getBoardLogResByTime(boardLogs, revObject.getId(), startTime, endTime);
        });

        return boardLogs;
    }

    private void getBoardLogResByType(List<GetBoardLogRes> boardLogs, Long rev, String revType) {

        String rType = revType;

        Revisions<Long, Board> revisions = boardRepository.findRevisions(rev);

        for (Revision<Long, Board> revision : revisions.getContent()) {
            if (String.valueOf(revision.getMetadata().getRevisionType()).equals(rType)) {
                boardLogs.add(makeGetBoardLogRes(revision));
            }
        }
    }

    private void getBoardLogRes(List<GetBoardLogRes> boardLogs, Long rev) {

        Revisions<Long, Board> revisions = boardRepository.findRevisions(rev);
        for (Revision<Long, Board> revision : revisions.getContent()) {
            boardLogs.add(makeGetBoardLogRes(revision));
        }
    }

    private void getBoardLogResByTime(List<GetBoardLogRes> boardLogs, Long rev,
                                     LocalDateTime startTime, LocalDateTime endTime) {

        Revisions<Long, Board> revisions = boardRepository.findRevisions(rev);
        for (Revision<Long, Board> revision : revisions.getContent()) {
            Instant requiredRevisionInstant = revision.getMetadata().getRequiredRevisionInstant();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(requiredRevisionInstant, ZoneId.of("Asia/Seoul"));

            if (!localDateTime.isBefore(startTime) && !localDateTime.isAfter(endTime)) {
                GetBoardLogRes getBoardLogRes = makeGetBoardLogRes(revision);
                boardLogs.add(getBoardLogRes);
            }

        }
    }

    private GetBoardLogRes makeGetBoardLogRes(Revision<Long, Board> revision) {
        Long revisionNumber = revision.getMetadata().getRevisionNumber().get();
        String revisionType = String.valueOf(revision.getMetadata().getRevisionType());

        Instant requiredRevisionInstant = revision.getMetadata().getRequiredRevisionInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(requiredRevisionInstant, ZoneId.of("Asia/Seoul"));
        return new GetBoardLogRes(revisionNumber, revisionType, localDateTime);
    }

    private List<Object> getRevs() {
        return auditReader.createQuery()
                .forRevisionsOfEntity(Board.class, false, true)
                .getResultList();
    }

    // PATCH
    public void modifyBoardContent(Long boardId, PatchBoardReq patchPostReq) {
        Board board = boardRepository.findByIdAndState(boardId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_BOARD));
        board.updateContent(patchPostReq.getContent());
    }

    public void modifyBoardState(Long boardId, State state) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BaseException(NOT_FIND_BOARD));
        board.updateState(state);
    }


    // DELETE
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findByIdAndState(boardId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_BOARD));
        boardRepository.delete(board);
    }

}
