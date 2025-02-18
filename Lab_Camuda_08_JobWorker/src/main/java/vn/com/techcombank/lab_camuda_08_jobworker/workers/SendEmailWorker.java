package vn.com.techcombank.lab_camuda_08_jobworker.workers;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendEmailWorker {

    @JobWorker(type="send_email")
    public void sendEmail(ActivatedJob job) {
        // Lấy giá trị input từ BPMN Process
        Map<String, Object> variables = job.getVariablesAsMap();

        System.out.println("🔄 [START] send email...");
        System.out.println("🔄 [JOB VARIABLES] " + variables);

        boolean inStock = Math.random() > 0.5; // Giả lập kiểm tra kho

        System.out.println("🔄 [END] send email success...");
    }
}
