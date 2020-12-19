package com.elcom.notify.repository;

import com.elcom.notify.model.dto.PatientByEmployeeDTO;
import com.elcom.notify.utils.StringUtil;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anhdv
 */
@Repository
public class HospitalCustomizeRepository extends BaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(HospitalCustomizeRepository.class);

    @Autowired
    public HospitalCustomizeRepository(EntityManagerFactory factory) {
        super(factory);
    }

    public List<PatientByEmployeeDTO> findPatientByEmployee(String employeeId) {
        Session session = openSession();
        try {
            String sql = " SELECT p.id AS patientId, p.full_name AS patientName, p.patient_code AS patientCode " +
                         " , CAST(ROUND(DATEDIFF(CURDATE(), p.birth_date)/ 365, 0) AS CHAR) as birthDate, p.gender as gender " +
                         " FROM patient p INNER JOIN employee_patient ep ON p.id = ep.patient_id " +
                         " WHERE ep.employee_id = :employeeId ORDER BY p.id ";
            return session.createNativeQuery(sql)
                    .setParameter("employeeId", employeeId)
                    .setResultTransformer(Transformers.aliasToBean(PatientByEmployeeDTO.class))
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        } finally {
            closeSession(session);
        }
        return null;
    }
}
