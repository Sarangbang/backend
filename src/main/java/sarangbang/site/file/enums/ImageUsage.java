package sarangbang.site.file.enums;

/**
 * 📋 이미지 사용 용도 열거형
 * 
 * 🎯 목적: 이미지가 어떤 용도로 사용되는지 구분
 * 
 * 📂 저장 경로 결정:
 * - PROFILE → profiles/ 폴더
 * - CHALLENGE → challenges/ 폴더  
 * - VERIFICATION → verifications/ 폴더
 * 
 * 🔐 인증 정책:
 * - PROFILE: 로그인 불필요 (회원가입 시 사용)
 * - CHALLENGE, VERIFICATION: 로그인 필수
 */
public enum ImageUsage {
    
    /**
     * 👤 프로필 이미지
     * - 회원가입 시 업로드
     * - 🔓 인증 불필요
     */
    PROFILE("프로필 이미지"),
    
    /**
     * 🏆 챌린지 대표 이미지
     * - 챌린지 생성 시 업로드
     * - 🔐 인증 필요
     */
    CHALLENGE("챌린지 대표 이미지"),
    
    /**
     * ✅ 인증 사진
     * - 챌린지 인증 시 업로드
     * - 🔐 인증 필요
     */
    VERIFICATION("인증 사진");
    
    private final String description;
    
    ImageUsage(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 📁 용도별 저장 경로 반환
     * 
     * @return 저장 경로 (예: "profiles/", "challenges/")
     */
    public String getStoragePath() {
        return switch (this) {
            case PROFILE -> "profiles/";
            case CHALLENGE -> "challenges/";
            case VERIFICATION -> "verifications/";
        };
    }
}
