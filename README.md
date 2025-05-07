
# 🧾 자리있나 (Table Eat Now)

**식당 예약 서비스를 중심으로 식당관리, 사용자 관리, 예약, 결제, 웨이팅, 쿠폰, 이벤트, 리뷰 등 다양한 기능을 통합한 통합 플랫폼**

---

## 🎯 서비스/프로젝트 목표

- **MAU 300만** 고객 대상 식당 예약 서비스 구축
- **7,000개 가맹점** 지원
- **식당별 최대 10,000명 동시 예약** 처리
- **프로모션 참여자 최대 1,000명** 동시 참여 안정성 확보

---

## 🏗️ 인프라 아키텍처

<img width="711" alt="스크린샷 2025-05-08 오전 12 07 03" src="https://github.com/user-attachments/assets/b2d61fc5-2596-4007-92ba-b07314ac7df7" />

- 인프라 아키텍쳐 상세 
<img width="1072" alt="스크린샷 2025-05-08 오전 12 10 03" src="https://github.com/user-attachments/assets/1fc84216-60ca-48be-a68c-14b09e33a8b2" />



### CI/CD Workflow

```mermaid
sequenceDiagram
    participant Developer
    participant GitHub
    participant Workflow (detect-changes)
    participant Workflow (deploy)
    participant AWS Elastic Beanstalk

    Developer->>GitHub: Push to feat/#222 or main branch
    GitHub->>Workflow (detect-changes): Trigger workflow
    Workflow (detect-changes)->>GitHub: Fetch git history & changed files
    Workflow (detect-changes)->>Workflow (deploy): Output list of changed modules
    alt If changed modules exist
        Workflow (deploy)->>AWS Elastic Beanstalk: Deploy each changed module (parallel)
    end
````

---

## 🔧 주요 기능

### 식당

* 식당 개설 / 정보 관리 / 타임슬롯 관리 / 리뷰 평점 업데이트 / 조회

### 예약

* 예약 신청 / 취소 / 상태 관리 / 예약 조회

### 대기 (웨이팅)

* 당일 웨이팅 / 대기 시간 안내 / 알림 / 상태 관리 / 취소

### 리뷰

* 평점 등록 / 수정 / 공개 범위 제어 / 주기적 갱신 / 필터링

### 결제

* 토스페이먼츠 연동 / 부분 취소 / 전액 환불

### 알림

* 식당 및 결제 상태 알림 (Slack/Email) / 예약 리마인더 / 프로모션 알림

### 쿠폰

* 다양한 쿠폰 타입 / 재고 관리 / 핫딜 / 프로모션 쿠폰

### 프로모션

* 참여형 프로모션 / 예약 실행 관리 / 성능 최적화 대응

---

## ⚙️ 기술 스택

### 프레임워크 & 라이브러리

* Java 17, Spring Boot 3.4.4, Spring Cloud, Spring Data JPA, QueryDSL, Redisson

### 데이터베이스

* PostgreSQL, Redis

### 메시징

* Apache Kafka

### 모니터링

* Prometheus, Grafana, Zipkin

### 인프라

* Docker, AWS EC2, Elastic Beanstalk, CloudWatch, GitHub Actions

### 기타

* SonarQube, JaCoCo, JMeter, CodeRabbit, Slack, Notion, Toss Payments

---

## 🚨 트러블슈팅

* [✅ 스케줄러 분산락 적용](https://www.notion.so/1e42dc3ef5148040abc5f50e95a274da?pvs=21)
* [✅ 알림 Polling → Kafka/Redis 개선](https://www.notion.so/DB-Polling-1e42dc3ef51480299cd0efb72785c4d8?pvs=21)
* [✅ Cursor + FixedDelay 기반 스케줄러 개선](https://www.notion.so/Cursor-FixedDelay-1e42dc3ef5148006a53bec06bfd4d3ba?pvs=21)
* [✅ GitHub Actions 활용 개선](https://www.notion.so/Github-actions-1e42dc3ef5148097b55ed56d1bdb313b?pvs=21)
* [✅ 프로모션 부하 테스트 및 최적화](https://www.notion.so/1e42dc3ef51480bba464d4d02fcb8895?pvs=21)

---

## 🧑‍💻 Contributors

| 이름      | 포지션                             | 기여 내용                                                                                       | 테스트 수 |
| ------- | ------------------------------- | ------------------------------------------------------------------------------------------- | ----- |
| **한지훈** | 팀장<br/>Notification/Promotion   | 전략 패턴 기반 알림/프로모션 처리<br/>Kafka, Redis 딜레이 큐 사용<br/>DLQ, 루아 스크립트, 모니터링 구성                     | 96    |
| **박지은** | 테크리더<br/>Restaurant/Reservation | 비관락, Kafka 배치, DLQ 처리<br/>예약 유효성 검증 전략 구성<br/>GitHub Actions 기반 테스트 자동화                     | 146   |
| **황하온** | Coupon/Waiting                  | 쿠폰 전략/중복 방지 처리<br/>루아 스크립트, ZSet, Redis 활용<br/>ZSet 기반 대기열 관리                               | 77    |
| **강혜주** | Payment/Review                  | 분산락 + 커서 기반 배치 최적화<br/>Kafka DLQ/리트라이 + Prometheus 지표 수집<br/>RestClient PG 연동 및 데코레이터 패턴 적용 | 255   |

---

## 📊 API 현황

```bash
- GET: 39
- POST: 15
- PATCH: 11
- PUT: 5
- DELETE: 6
- TOTAL: 76
```

---

## 📁 GitHub 활동

* **Issues:** 102건
* **Pull Requests:** 106건
* **Test Files:** 총 562개
* **Test Coverage:** 프레젠테이션/애플리케이션 레이어 97.4% 이상

---

## 📬 문의

> 기술적 내용, 구조 및 설계 관련 문의는 각 담당자 GitHub 통해 이슈 남겨주세요.

