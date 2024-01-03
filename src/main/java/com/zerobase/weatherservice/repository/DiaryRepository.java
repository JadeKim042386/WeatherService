package com.zerobase.weatherservice.repository;

import com.zerobase.weatherservice.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface DiaryRepository extends JpaRepository<Diary, Integer> {
}
