/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vitalsign.service;

import com.elcom.vitalsign.model.measuredata.DataBp;
import com.elcom.vitalsign.model.measuredata.DataSpo2;
import com.elcom.vitalsign.model.measuredata.DataTemp;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface DataService {
    
    void saveDataBp(List<DataBp> item);

    void saveDataSpo2(List<DataSpo2> item);

    //void saveDataRr(List<DataRr> item);
    void saveDataTemp(List<DataTemp> item);

    void addDataPartition(String[] tables);

}
