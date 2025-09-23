package picklunch.config;

import picklunch.model.entity.User;
import picklunch.repository.UserRepo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LoadUsersJobConfig {

    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_ENCODED_PASSWORD = "encoded_password";
    public static final String FIELD_DISPLAY_NAME = "display_name";

    @Value("${picklunch.files.users}")
    private String usersFile;

    @Autowired
    private UserRepo userRepo;

    @Bean
    public Job loadUsersJob(JobRepository jobRepository, Step loadUsersStep, Step deleteAllUsersStep) {
        return new JobBuilder("loadUsersJob", jobRepository)
                .start(deleteAllUsersStep)
                .next(loadUsersStep)
                .build();
    }

    @Bean
    public Step deleteAllUsersStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("deleteAllUsersStep", jobRepository)
                .tasklet(deleteAllUsersTasklet(), transactionManager)
                .build();
    }

    private Tasklet deleteAllUsersTasklet() {
        return (unused_1, unused_2) -> {
            userRepo.deleteAll();
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step loadUsersStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("loadUsersStep", jobRepository)
                .<User, User>chunk(10, transactionManager)
                .reader(csvUserItemReader())
                .writer(userRepoWriter())
                .build();
    }

    private ItemReader<User> csvUserItemReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("csvUserItemReader")
                .resource(new FileSystemResource(usersFile))
                .delimited()
                .names(new String[]{
                        FIELD_USERNAME,
                        FIELD_DISPLAY_NAME,
                        FIELD_ENCODED_PASSWORD
                })
                .fieldSetMapper(fieldSet -> User.builder()
                        .username(fieldSet.readString(FIELD_USERNAME))
                        .displayName(fieldSet.readString(FIELD_DISPLAY_NAME))
                        .encodedPassword(fieldSet.readString(FIELD_ENCODED_PASSWORD))
                        .build())
                .build();
    }

    private ItemWriter<User> userRepoWriter() {
        return new RepositoryItemWriterBuilder<User>()
                .repository(userRepo)
                .build();
    }

}
