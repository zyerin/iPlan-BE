package com.example.iplan.auth;

// 유저 정보를 Firestore 에 저장하기 위한 리포지토리
import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import org.springframework.stereotype.Repository;
import com.google.cloud.firestore.Firestore;

@Repository
public class UserSocialRepository extends DefaultFirebaseDBRepository<UserSocial> {

    public UserSocialRepository(Firestore firestore) {
        super(firestore);
        setEntityClass(UserSocial.class);
        setCollectionName("User");
    }
}