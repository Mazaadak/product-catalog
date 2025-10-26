package com.mazadak.product_catalog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mazadak.product_catalog.workflow.activity.CreateListingActivities;
import com.mazadak.product_catalog.workflow.activity.impl.CreateListingActivitiesImpl;
import com.mazadak.product_catalog.workflow.impl.ListingCreationWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.common.converter.JacksonJsonPayloadConverter;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalWorkerConfig {

    @Value("${temporal.address:localhost:7233}")
    private String temporalAddress;

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        // Connect to Temporal server using configured address
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(temporalAddress)
                        .build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs service) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new JavaTimeModule());

        JacksonJsonPayloadConverter jsonConverter = new JacksonJsonPayloadConverter(objectMapper);

        DefaultDataConverter dataConverter = DefaultDataConverter.newDefaultInstance()
                .withPayloadConverterOverrides(jsonConverter);

        return WorkflowClient.newInstance(
                service,
                WorkflowClientOptions.newBuilder()
                        .setDataConverter(dataConverter)
                        .build()
        );
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient) {
        return WorkerFactory.newInstance(workflowClient);
    }

    @Bean
    public Worker listingCreationWorkflow(
            WorkerFactory workerFactory,
            CreateListingActivitiesImpl activities
    ) {
        Worker worker = workerFactory.newWorker("LISTING_CREATION_TASK_QUEUE");

        worker.registerWorkflowImplementationTypes(
                ListingCreationWorkflowImpl.class
        );

        worker.registerActivitiesImplementations(
                activities
        );

        return worker;
    }

    @Bean
    public ApplicationRunner startWorkerFactory(WorkerFactory workerFactory) {
        return args -> workerFactory.start();
    }
}