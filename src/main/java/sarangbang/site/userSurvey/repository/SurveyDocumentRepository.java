package sarangbang.site.userSurvey.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sarangbang.site.userSurvey.entity.SurveyDocument;

@Repository
public interface SurveyDocumentRepository extends MongoRepository<SurveyDocument, String> {
}