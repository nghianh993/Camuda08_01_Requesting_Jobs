package vn.com.techcombank.lab_camuda_08_jobworker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Deployment(resources = "classpath:order-fulfillment-process.bpmn")
public class LabCamuda08JobWorkerApplication implements CommandLineRunner {
    private final ZeebeClient zeebeClient;

    public LabCamuda08JobWorkerApplication(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(LabCamuda08JobWorkerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        zeebeClient.getConfiguration();
        System.out.println("✅ Camunda 8 Zeebe Job Workers started successfully!");
    }
}
