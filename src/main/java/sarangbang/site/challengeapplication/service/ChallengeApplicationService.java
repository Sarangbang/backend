package sarangbang.site.challengeapplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sarangbang.site.challengeapplication.repository.ChallengeApplicationRepository;

@Service
@RequiredArgsConstructor
public class ChallengeApplicationService {

    private final ChallengeApplicationRepository challengeApplicationRepository;
}
