package com.example.iplan.Repository;

// 유저 정보를 Firestore 에 저장하기 위한 리포지토리
import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import com.example.iplan.Domain.UserSocial;
import org.springframework.stereotype.Repository;
import com.google.cloud.firestore.Firestore;

@Repository
public class UserRepository extends DefaultFirebaseDBRepository<UserSocial> {

    public UserRepository(Firestore firestore) {
        // super(firestore);
        setEntityClass(UserSocial.class);
        setCollectionName("User");
    }
}