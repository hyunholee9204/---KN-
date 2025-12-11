package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.Entity.ChangeType;
import com.example.CapstonProject0.Entity.UpdateDetailEntity;
import com.example.CapstonProject0.Entity.UpdateEntity;
import com.example.CapstonProject0.Entity.UpdateStatus;
import com.example.CapstonProject0.Repository.UpdateDetailRepository;
import com.example.CapstonProject0.Repository.UpdateRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {

    private final UpdateRepository repo;
    private final UpdateDetailRepository detailRepo;

    public UpdateService(UpdateRepository repo, UpdateDetailRepository detailRepo) {
        this.repo = repo;
        this.detailRepo = detailRepo;
    }

    public Page<UpdateEntity> list(UpdateStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.desc("pinned"),
                Sort.Order.desc("releaseDate"),
                Sort.Order.desc("id")
        ));

        Page<UpdateEntity> result;

        if (status == null) {
            result = repo.findAll(pageable);
        } else {
            result = repo.findAllByStatusOrderByPinnedDescReleaseDateDesc(status, pageable);
        }

        // ✅ 방어 코드: 혹시라도 null 리턴될 경우 빈 페이지 반환
        if (result == null) {
            return Page.empty(pageable);
        }

        return result;
    }

    /**
     * ✅ 특정 업데이트 상세 조회
     */
    public UpdateEntity get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 업데이트 내역을 찾을 수 없습니다. (id=" + id + ")"));
    }

    /**
     * ✅ 업데이트 저장 (관리자 작성용)
     */
    @Transactional
    public UpdateEntity save(UpdateEntity note) {
        return repo.save(note);
    }

    /**
     * ✅ 세부 변경 내용 추가
     */
    @Transactional
    public void addChange(Long noteId, ChangeType type, String desc) {
        var note = get(noteId);
        var change = new UpdateDetailEntity();
        change.setNote(note);
        change.setType(type);
        change.setDescription(desc);
        detailRepo.save(change);
    }

    /**
     * ✅ 업데이트 삭제
     */
    @Transactional
    public void delete(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
        } else {
            throw new IllegalArgumentException("삭제할 업데이트가 존재하지 않습니다. (id=" + id + ")");
        }
    }
}
