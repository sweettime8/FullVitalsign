package com.elcom.id.schedulers;

//import com.elcom.id.controller.UserController;
//import com.elcom.id.model.User;
//import com.elcom.id.service.UserService;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

/**
 *
 * @author anhdv
 */
@Service
public class Schedulers {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Schedulers.class);
    
    //@Autowired
    //private UserService userService;
    
    //@Autowired
    //private UserController userController;
    
    // job in ra thời gian hiện tại, set delay 2 giây giữa những lần xử lý, sync, xử lý xong thì mới bắt đầu tính delay (Sync)
    @Scheduled(fixedDelay = 200000)
    public void timePrint() throws InterruptedException {
        LOGGER.info("[fixedDelay] - " + LocalDateTime.now());
        //List<User> userList = userService.findByStatus(1);
        //if(userList != null && userList.size() > 0){
        //    for(User user : userList){
        //        userController.notifyGamifyService("POST", user.getUuid());;
        //    }
        //}
    }
    
    // Giống fixedDelay nhưng chạy A-sync
    //@Scheduled(fixedRate = 2000)
    public void scheduleFixedRateTask() throws InterruptedException {
      LOGGER.info("[fixedRate] - " + LocalDateTime.now());
    }
    
    /*
     Dùng biểu thức cron tùy biến để chạy job, list biểu thức: https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
     vd: job này 5 phút chạy một lần, bắt đầu từ 09h00 đến 09h55 (ko có đến 10h vì 5' sau cùng của tiếng thứ 9 ko nằm trong giới hạn 9)
    */
    //@Scheduled(cron = "0 0/5 9 * * ?")
    public void scheduleTaskUsingCronExpression() throws InterruptedException {
      LOGGER.info("[cron expression] - " + LocalDateTime.now());
    }
    
    @Bean
    public TaskScheduler taskScheduler() {
      final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
      scheduler.setPoolSize(10);
      return scheduler;
    }
}
