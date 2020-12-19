package com.elcom.fileservice.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.elcom.fileservice.messaging.rabbitmq.RabbitMQClient;
import com.elcom.fileservice.model.dto.DataBpDTO;
import com.elcom.fileservice.model.dto.DataBpRowsDTO;
import com.elcom.fileservice.model.dto.DataTempDTO;
import com.elcom.fileservice.model.dto.DataMeasureMapsDTO;
import com.elcom.fileservice.model.dto.DataSpo2DTO;
import com.elcom.fileservice.model.dto.DataSpo2RowsDTO;
import com.elcom.fileservice.model.dto.DataTempRowsDTO;
import com.elcom.fileservice.utils.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author anhdv
 */
@Controller
public class MeasureDataBusiness {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureDataBusiness.class);
    
//    @Autowired
//    private MeasureService measureService;
    
    @Autowired
    private RabbitMQClient rabbitMQClient;
    
    /** Xử lý dữ liệu đo nhiệt độ
     * @param file */
    public void saveDataTemp(File file) {
        DataTempDTO dataTempDTO = new DataTempDTO(new ArrayList<>());
        try (FileReader reader = new FileReader(file)) {
            String json = (String) new JSONParser().parse(reader);
            DataMeasureMapsDTO dataMeasureMapsDTO = (new ObjectMapper()).readValue(json, DataMeasureMapsDTO.class);
            dataTempDTO.setGateId(dataMeasureMapsDTO.getGateId());
            dataTempDTO.setPatientId(dataMeasureMapsDTO.getPatientId());

            int check = 0;
            boolean done;

            //map key va value trong json file
            DataTempRowsDTO dataRowDTO = new DataTempRowsDTO();
            for (int i = 0; i < dataMeasureMapsDTO.getRows().size(); i++) {
                done = false;
                if (check == 0 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setMeasureId(dataMeasureMapsDTO.getRows().get(i));
                }
                if (check == 1 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setTemp(Float.parseFloat(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 2 && !done) {
                    check = 0;
                    dataRowDTO.setTs(Long.parseLong(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 0)
                    dataTempDTO.getData().add(dataRowDTO);
            }
            //TODO Insert DB - Call workQueue VitalsignMeasureDataService
            if( !dataTempDTO.getData().isEmpty() ) {
                
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        } finally {
            LOGGER.info("extractFile.delete: {}", file.delete());
        }
    }
    
    /** Xử lý dữ liệu đo huyết áp
     * @param file */
    public void saveDataBp(File file) {
        DataBpDTO dataBpDTO = new DataBpDTO(new ArrayList<>());
        try (FileReader reader = new FileReader(file)) {
            String json = (String) new JSONParser().parse(reader);
            DataMeasureMapsDTO dataMeasureMapsDTO = (new ObjectMapper()).readValue(json, DataMeasureMapsDTO.class);
            dataBpDTO.setGateId(dataMeasureMapsDTO.getGateId());
            dataBpDTO.setPatientId(dataMeasureMapsDTO.getPatientId());

            int check = 0;
            boolean done;

            //map key va value trong json file
            DataBpRowsDTO dataRowDTO = new DataBpRowsDTO();
            for (int i = 0; i < dataMeasureMapsDTO.getRows().size(); i++) {
                done = false;
                if (check == 0 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setMeasureId(dataMeasureMapsDTO.getRows().get(i));
                }
                if (check == 1 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setSys(Integer.parseInt(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 2 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setDia(Integer.parseInt(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 3 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setMap(Integer.parseInt(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 4 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setPr(Integer.parseInt(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 5 && !done) {
                    check = 0;
                    dataRowDTO.setTs(Long.parseLong(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 0)
                    dataBpDTO.getData().add(dataRowDTO);
            }
            //Insert DB
            if( !dataBpDTO.getData().isEmpty() ) {
                
            }
        } catch (Exception ex) {
            LOGGER.error(StringUtil.printException(ex));
        }
        LOGGER.info("extractFile.delete: {}", file.delete());
    }
    
    /** Xử lý dữ liệu đo spo2
     * @param file */
    public void saveDataSpo2(File file) {
        DataSpo2DTO dataSpo2DTO = new DataSpo2DTO(new ArrayList<>());
        try (FileReader reader = new FileReader(file)) {
            String json = (String) new JSONParser().parse(reader);
            DataMeasureMapsDTO dataMeasureMapsDTO = (new ObjectMapper()).readValue(json, DataMeasureMapsDTO.class);
            dataSpo2DTO.setGateId(dataMeasureMapsDTO.getGateId());
            dataSpo2DTO.setPatientId(dataMeasureMapsDTO.getPatientId());

            int check = 0;
            boolean done;

            //map key va value trong json file
            DataSpo2RowsDTO dataRowDTO = new DataSpo2RowsDTO();
            for (int i = 0; i < dataMeasureMapsDTO.getRows().size(); i++) {
                done = false;
                if (check == 0 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setMeasureId(dataMeasureMapsDTO.getRows().get(i));
                }
                if (check == 1 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setSpo2(Integer.parseInt(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 2 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setPi(Integer.parseInt(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 3 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setPr(Double.parseDouble(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 4 && !done) {
                    check++;
                    done = true;
                    dataRowDTO.setStep(Integer.parseInt(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 5 && !done) {
                    check = 0;
                    dataRowDTO.setTs(Long.parseLong(dataMeasureMapsDTO.getRows().get(i)));
                }
                if (check == 0)
                    dataSpo2DTO.getData().add(dataRowDTO);
            }
            //Insert DB
            if( !dataSpo2DTO.getData().isEmpty() ) {
                
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.printException(e));
        }
        LOGGER.info("extractFile.delete: {}", file.delete());
    }
}
