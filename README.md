<div align="center">

# 💰 KN금융  
### **Personal Financial Management Platform**

📅 **2025.03 ~ 2025.11**  
👤 **1인 개발 (Full-Stack Web Project)**  

---

### 🔗 *Use your financial data more clearly, smarter, and efficiently.*

</div>

---

## ✨ 프로젝트 소개

**KN금융은 개인 금융 데이터를 한곳에서 관리하고 시각화하여,  
효율적인 금융 생활을 돕기 위해 개발된 웹 기반 통합 금융 플랫폼입니다.**

> “자산 관리, 대출 분석, 목표 트래킹, 금융 교육까지 한 번에!”

---

## 🚀 주요 기능 (Key Features)

### 🔐 **1. 사용자 인증 기능**
- 회원가입 / 로그인 / 로그아웃  
- 아이디/비밀번호 찾기  
- 회원 탈퇴  

---

### 💰 **2. 자산 관리 (Asset Management)**
- 자산 등록 / 수정 / 삭제 / 전체 삭제  
- 자산 현황 대시보드  
- 월별 자산 변동 그래프  
- 자산 내역 조회  
- 목표 전용 자산 등록 기능 (목표 트래킹 기능과 연동)

> 📊 *다양한 자산 종류를 자동 계산하여 시각적으로 보여주는 핵심 기능입니다.*

---

### 🏦 **3. 대출 관리 (Loan Management)**
- 대출 등록 / 수정 / 삭제  
- 총 대출금 / 이자율 / 평균값 자동 계산  
- 상환 일정표 제공  
- **대출 이자율 계산기**  
  - 원리금균등  
  - 원금균등  
  - 만기일시상환  

> 계산기 UI를 직접 구현하여 사용자 편의를 강화한 기능입니다.

---

### 🎯 **4. 목표 달성 트래킹 (Goal Tracker)**
- 목표 등록 / 수정 / 삭제  
- 진행률 자동 계산  
- 남은 기간 표시  
- 목표별 히스토리 관리  

> 🟩 *진행률 바 + 기간 계산이 포함된 완전 시각화 UI*

---

### 📚 **5. 교육 및 금융 정보 제공**
- 저축 / 예산 / 이자 개념 등 초보자용 금융 교육 콘텐츠  
- 최신 경제 뉴스 제공  
- 실시간 환율 정보  
- 최근 7일 환율 그래프 자동 시각화  

---

### 🛎 **6. 고객센터 기능**
- 공지사항  
- 업데이트 내역  
- 문의하기 (Q&A)  
- 내 문의 조회  
- FAQ  
- 이용 가이드  

---

<br>

# 🛠 기술 스택 (Tech Stack)

<div align="center">

| 영역 | 기술 |
|------|------|
| **Frontend** | HTML5, CSS3, JavaScript, Thymeleaf |
| **Backend** | Java 17, Spring Boot, Spring MVC, Spring Data JPA, Lombok |
| **Database** | MySQL, JPA 기반 Entity 구조 |
| **Tools** | Git/GitHub, IntelliJ IDEA, MySQL Workbench |

</div>

---

<br>

# 🗂 DB 구조 (Database Schema)
<div align="center">

| 테이블명 | 설명 |
|---------|-------|
| **asset** | 사용자가 등록한 자산 정보 |
| **asset_history** | 자산 변동 내역 |
| **education_list** | 교육/강의 콘텐츠 리스트 |
| **faq** | 고객센터 FAQ |
| **finance_history** | 금융 지표 저장 및 추세 그래프 |
| **inquiry** | 사용자가 남긴 문의사항 |
| **loans** | 대출 정보 관리 |
| **login_list** | 사용자 계정 정보 |
| **notice** | 공지사항 |
| **repayment** | 대출 상환 내역 |
| **target** | 목표 설정 정보 |
| **target_history** | 목표 달성 히스토리 |
| **transaction** | 거래 내역 |
| **updates** | 업데이트 내역 |
| **update_detail** | 업데이트 상세 내역 |

</div>

# 🖼 주요 화면 캡처 (Screenshots)

> ⚠ 이미지는 예시 경로입니다. GitHub에 이미지 업로드 후 경로만 바꿔주세요.  
> 예: `![메인페이지](https://github.com/hyunholee9204/---KN-/assets/12345/main.png)`

### 🌐 메인 페이지  
![Main](이미지경로)

### 💰 자산 현황  
![Asset](이미지경로)

### 🏦 대출 관리  
![Loans](이미지경로)

### 📉 이자율 계산기  
![LoanCalc](이미지경로)

### 🎯 목표 트래킹  
![Goal](이미지경로)

### 📚 교육 콘텐츠  
![Education](이미지경로)

### 💱 금융 정보 (환율 그래프)  
![Finance](이미지경로)

### 📢 공지사항  
![Notice](이미지경로)

---

# 📅 개발 정보 (Development Info)

- **개발 기간:** 2025.03 ~ 2025.11  
- **개발자:** 1인 개발  
- **프로젝트 유형:** 대학 캡스톤 디자인  
- **개발 방식:** Spring Boot 기반 MVC 아키텍처  

---

# ▶ 실행 방법 (How to Run)

```bash
# 1. Repository Clone
git clone https://github.com/사용자명/---KN-.git
cd 프로젝트폴더

# 2. Run Project (Windows)
gradlew.bat bootRun

# 3. Run Project (Mac/Linux)
./gradlew bootRun

