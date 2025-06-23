package sarangbang.site.userSurvey.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "surveys") //MongoDB용
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDocument {
    
    @Id
    private String id;
    private String title;
    private List<Category> categories;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Category {
        private String categoryId;
        private String name;
        private List<Question> questions;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Question {
        private String questionId;
        private String text;
        private List<Option> options;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option {
        private Integer value;
        private String text;
    }
}