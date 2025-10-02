package com.pgms.service;

import com.pgms.domain.Notice;
import com.pgms.repo.NoticeRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NoticeService {
    private final NoticeRepo noticeRepo;

    public NoticeService(NoticeRepo noticeRepo) {
        this.noticeRepo = noticeRepo;
    }

    public Notice create(Notice notice) {
        return noticeRepo.save(notice);
    }

    @Transactional(readOnly = true)
    public List<Notice> listByTenant(UUID tenantId) {
        return noticeRepo.findByTenant_Id(tenantId);
    }
}
