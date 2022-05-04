//package com.epam.esm.service.impl.v1;
//
//import com.epam.esm.dao.CertificateDao;
//import com.epam.esm.model.Certificate;
//import com.epam.esm.model.dto.CertificateCreateDto;
//import com.epam.esm.model.dto.CertificateUpdateDto;
//import com.epam.esm.service.CertificateService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Component;
//import org.springframework.util.MultiValueMap;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public abstract class CertificateServiceImplV1 implements CertificateService {
//
//    @Autowired
//    private final CertificateDao certificateDao;
//
//    public CertificateServiceImplV1(CertificateDao certificateDao) {
//        this.certificateDao = certificateDao;
//    }
//
//    @Override
//    public Certificate createCertificate(CertificateCreateDto dto) {
//        Date createdAt = new Date();
//        return certificateDao.createCertificate(dto, createdAt);
//    }
//
//    @Override
//    public Certificate getCertificate(long id) {
//        return certificateDao.getCertificateById(id);
//    }
//
//    @Override
//    public List<Certificate> getAllCertificates(int limit, long offset) {
//        return certificateDao.getAllCertificates(limit, offset);
//    }
//
//    @Override
//    public Certificate updateCertificate(long id, MultiValueMap<String, String> params) {
//        CertificateUpdateDto dto = new CertificateUpdateDto();
//
//        dto.setId(id);
//        if (params.containsKey("name")) dto.setName(params.getFirst("name"));
//        if (params.containsKey("description")) dto.setDescription(params.get("description"));
//        if (params.containsKey("price")) dto.setPrice(Integer.parseInt(params.getFirst("price")));
//        if (params.containsKey("duration")) dto.setDuration(Integer.parseInt(params.getFirst("duration")));
//        dto.affixUpdateTimestamp();
//
//        return certificateDao.update(dto);
//    }
//
//    @Override
//    public boolean deleteCertificate(long id) {
//        return certificateDao.delete(id);
//    }
//
//    @Override
//    public List<Certificate> searchCertificates(int limit, long offset, Map<String, String> parameters) {
//        return certificateDao.searchCertificates(limit, offset, parameters);
//    }
//
////    @Override
//    public Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, int pageNumber, int pageSize) {
//        return certificateDao.searchCertificatesByTagNames(tagNames, pageNumber, pageSize);
//    }
//
//}
