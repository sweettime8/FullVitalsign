/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.id.repository;

import com.elcom.id.model.dto.DoctorPatientMapDTO;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ducnh
 */
@Repository
public class DoctorPatientMapCustomizeRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorPatientMapCustomizeRepository.class);

    @Autowired
    public DoctorPatientMapCustomizeRepository(EntityManagerFactory factory) {
        super(factory);
    }

    public List<DoctorPatientMapDTO> findByPatientId(String uuid) {
        Session session = openSession();
        try {
            String sql = "select u.uuid AS doctorId, u.full_name AS doctorFullName, u.description\n"
                    + "FROM doctor_patient_map dum INNER JOIN user u ON dum.doctor_id = u.uuid \n"
                    + "WHERE u.is_delete = 0 AND u.status = 1 AND dum.active = 1 AND dum.patient_id = :id";

            NativeQuery query = session.createNativeQuery(sql);
            query.setParameter("id", uuid);

            query.addScalar("doctorId", StandardBasicTypes.STRING);
            query.addScalar("doctorFullName", StandardBasicTypes.STRING);
            query.addScalar("description", StandardBasicTypes.STRING);

            query.setResultTransformer(Transformers.aliasToBean(DoctorPatientMapDTO.class));
            Object result = query.list();

            return result != null ? (List<DoctorPatientMapDTO>) result : null;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            closeSession(session);
        }
        return null;
    }
}
