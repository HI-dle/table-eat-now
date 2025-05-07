# 🧾 **자리있나 (Table Eat Now)**


<p align="center">
  <img width="700" alt="스크린샷 2025-05-08 오전 12 33 29" src="https://github.com/user-attachments/assets/fd12d83b-4a80-46ba-9478-9452ab11c8f4" />
</p>

**Table Eat Now**는  
**식당 예약 · 웨이팅 · 결제 · 리뷰 · 쿠폰 · 이벤트** 등  
고객과 식당을 위한 **All-in-One 통합 예약 플랫폼**입니다.

> 고객에게는 **편리한 예약 경험**을,  
> 식당에게는 **효율적인 운영 관리 도구**를 제공합니다.


## 🔗 목차

[![기술 스택](https://img.shields.io/badge/🛠️-기술_스택-24435C?style=flat-square\&logoColor=white)](#️-기술-스택)
[![설계 산출물](https://img.shields.io/badge/📐-설계_산출물-2E556E?style=flat-square\&logoColor=white)](#-설계-산출물)
[![트러블슈팅](https://img.shields.io/badge/🔍-트러블슈팅-2E556E?style=flat-square\&logoColor=white)](#-트러블슈팅)
[![요구사항](https://img.shields.io/badge/📝-요구사항-1F3A53?style=flat-square\&logoColor=white)](#-요구사항)
[![팀원 소개](https://img.shields.io/badge/👥-팀원_소개-1A314A?style=flat-square\&logoColor=white)](#-팀원-소개)

---

## 📌 프로젝트 개요

**식당 예약 서비스 플랫폼**

* 식당, 사용자, 예약, 결제, 웨이팅, 리뷰 등 통합 관리 서비스
* 7,000개 가맹점, 식당별 최대 10,000명 동시 예약 처리
* 프로모션 동시 참여자 1,000명 이상을 고려한 시스템 안정성 확보

---

## 🛠️ 기술 스택

| 분류         | 기술                                                                 |
| ---------- | ------------------------------------------------------------------ |
| Language   | Java 17                                                            |
| Framework  | Spring Boot 3.4.4, Spring Cloud (Eureka, Gateway, OpenFeign)       |
| DB         | PostgreSQL, Redis                                                  |
| ORM        | Spring Data JPA, QueryDSL 5.0.0                                    |
| Messaging  | Apache Kafka                                                       |
| Infra      | Docker, AWS EC2, AWS Elastic Beanstalk, CloudWatch, GitHub Actions |
| Monitoring | Prometheus, Grafana, Zipkin                                        |
| Testing    | JUnit 5, Mockito, JaCoCo, SonarQube                                |
| Tools      | Notion, Slack, GitHub, Toss Payments                               |

---

## 📐 설계 산출물

* **API 명세**: [API 정의서 (Notion)](https://climbing-centipede-b7f.notion.site/API-1cc67f8e8327800aa312d744a92a162b?pvs=4)
* **ERD**: [ERD 보기 (Notion)](https://climbing-centipede-b7f.notion.site/ERD-1ca67f8e832780cda0cbd92cb0e6213c?pvs=4)
* **아키텍처**: [아키텍처 개요](https://github.com/HI-dle/table-eat-now/wiki/architecture)

---

## 🔍 트러블슈팅

* [스케줄러 분산락 적용](https://github.com/HI-dle/table-eat-now/wiki/%EC%8A%A4%EC%BC%80%EC%A4%84%EB%9F%AC-%EB%B6%84%EC%82%B0%EB%9D%BD-%EC%A0%81%EC%9A%A9)
* [DB Polling 방식 리팩토링](https://github.com/HI-dle/table-eat-now/wiki/%EC%95%8C%EB%A6%BC-DB-Polling-%EB%B0%A9%EC%8B%9D-%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81%EC%9C%BC%EB%A1%9C-%EC%84%B1%EB%8A%A5-%EC%B5%9C%EC%A0%81%ED%99%94)
* [Cursor + FixedDelay 기반 스케줄러 전환](https://github.com/HI-dle/table-eat-now/wiki/Cursor---FixedDelay-%EA%B8%B0%EB%B0%98-%EC%8A%A4%EC%BC%80%EC%A4%84%EB%9F%AC-%EC%A0%84%ED%99%98)
* [GitHub Actions 개선](https://github.com/HI-dle/table-eat-now/wiki/Github-actions-%ED%99%9C%EC%9A%A9-%EB%B0%8F-%EA%B0%9C%EC%84%A0)
* [프로모션 성능 테스트](https://github.com/HI-dle/table-eat-now/wiki/%ED%94%84%EB%A1%9C%EB%AA%A8%EC%85%98-%EC%84%B1%EB%8A%A5-%EB%AA%A9%ED%91%9C%EC%B9%98-%ED%85%8C%EC%8A%A4%ED%8A%B8)

---

## 📝 요구사항

* 식당, 예약, 리뷰, 결제, 쿠폰, 프로모션 등 CRUD 및 동시성 고려
* MSA 구조에 기반한 도메인 분리 및 서비스 간 Kafka 연동
* 실시간 예약 및 대기열 처리, 리뷰 평점 주기적 갱신, 알림 시스템
* 테스트 커버리지 80% 이상 기준으로 PR 제한 (SonarQube, JaCoCo 연동)

---

## 👥 팀원 소개

| 이름  | 포지션                               | 주요 기여                                                                                            |
| --- | --------------------------------- | ------------------------------------------------------------------------------------------------ |
| 한지훈 | 팀장<br/>Notification / Promotion   | Kafka 기반 알림 시스템 / Redis 딜레이 큐 / DLQ 및 모니터링 / 프로모션 성능 최적화 및 원자성 보장                                |
| 박지은 | 테크리더<br/>Restaurant / Reservation | 예약 유효성 검증 전략 / Kafka 상태 관리 / DLQ / FeignClient 통한 멱등성 관리 / 테스트 자동화 환경 구성                         |
| 황하온 | Coupon / Waiting                  | 쿠폰 전략/루아스크립트 처리 / Redis ZSet 기반 대기열 처리 / 이벤트 중복 방지 / GitHub Actions 속도 개선                        |
| 강혜주 | Review / Payment                  | Redisson 분산락 + 커서 기반 배치 최적화 / RestClient PG 연동 / Kafka DLQ + 지표 수집 / 데코레이터 패턴 기반 TaskExecutor 구성 |
