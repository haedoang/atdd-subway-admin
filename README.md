<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-%3E%3D%205.5.0-blue">
  <img alt="node" src="https://img.shields.io/badge/node-%3E%3D%209.3.0-blue">
  <a href="https://edu.nextstep.camp/c/R89PYi5H" alt="nextstep atdd">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/next-step/atdd-subway-admin">
</p>

<br>

# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
#### application 구동
```
./gradlew bootRun
```
<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.



---
### atdd-subway-web 도커 컨테이너로 실행하기

#### 1. Dockerfile 복사해서 워크스페이스 경로에 넣기

#### 2. 워크스페이스 경로로 이동
```cd ${workspace}```

#### 3. 프록시 설정 변경(로컬 아이피 넣기)
 - 127.0.0.1 => 로컬 아이피주소
![image info](./image.png) 

#### 3.도커파일 빌드
 - frontend 내 node_modules 디렉터리가 있으면 정상적으로 빌드되지 않음. 삭제 후 빌드
 - ``` docker build -t atdd-web:0.0 .```

#### 4.도커 컨테이너 실행
 - ```docker run -it --name atdd_web -p 8081:8081 atdd-web:0.0```
---


## 1단계(step1) - 지하철 노선 관리

#### 요구사항 1
- [x] : 기능 목록: 생성/목록 조회/조회/수정/삭제
- [x] : 기능 구현 전 인수 테스트 작성 
- [x] : 기능 구현 후 인수 테스트 리팩터링

#### 요구사항 설명 
- ##### 지하철 노선 관련 기능의 인수 테스트를 작성하기
  - ```LinkAcceptanceTest```를 완성시키세요.
- ##### 지하철 노선 관련 기능 구현하기
  - 인수 테스트가 모두 성공할 수 있도록 ```LineController```를 통해 요청을 받고 처리하는 기능을 구현하세요.
- ##### 인수 테스트 리팩터링
  - 인수 테스트의 각 스텝들을 메서드로 분리하여 재사용하세요.
    - ex) 인수 테스트 요청 로직 중복 제거 등 
  
#### 힌트 
- ##### RestAssured  [link](https://github.com/rest-assured/rest-assured/wiki/Usage#examples)
  - given 
    - 요청을 위한 값을 설정(header, content Type 등)
    - body가 있는 경우 body 값을 설정함
  - when 
    - 요청의 url과 method를 설정
  - then
    - 응답의 결과를 관리
    - response를 추출하거나 response값을 관리할 수 있음 

--- 


## 2단계(step2) - 인수 테스트 리팩터링

#### 요구사항 - API 변경 대응하기
- [x] : 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기. 두 종점역은 `구간` 형태로 관리되어야 함
- [x] : 노선 조회 시 응답 결과에 역 목록 추가하기. `상행역 부터 하행역 순으로 정렬되어야 함`
- [x] : 기능 구현 후 인수 테스트 리팩터링

#### 요구사항 설명
- ##### 노선 생성 시 두 종점역 추가하기
  - 인수 테스트와 DTO 등 수정이 필요함

- ##### 노선 객체에서 구간 정보를 관리하기
  - 노선 생성시 전달되는 두 종점역은 노선의 상태로 관리되는 것이 아니라 구간으로 관리되어야 함

- ##### 노선의 역 목록을 조회하는 기능 구현하기
  - 노선 조회 시 역 목록을 함께 응답할 수 있도록 변경
  - 노선에 등록된 구간을 순서대로 정렬하여 상행 종점부터 하행 종점까지 목록을 응답하기
  - 필요시 노선과 구간(혹은 역)의 관계를 새로 맺기

#### 힌트
- ##### 기능 변경 시 인수 테스트를 먼저 변경하기
  - 기능(혹은 스펙) 변경 시 테스트가 있는 환경에서 프로덕션 코드를 먼저 수정할 경우 어려움을 겪을 수 있음
    - 프로덕션 코드를 수정하고 그에 맞춰 테스트 코드를 수정해 주어야 해서 두번 작업하는 느낌
  - 항상 테스트를 먼저 수정한 다음 프로덕션을 수정하자!
  - 더 좋은 방법은 기존 테스트는 두고 새로운 테스트를 먼저 만들고 시작하자!

--- 

## 3단계(step3) - 구간 추가 기능

#### 요구사항 - 지하철 구간 등록 기능을 구현하기
- [x] 기능 구현 전 인수 테스트 작성
- [x] 예외 케이스 처리 인수 테스트 작성

#### 요구사항 설명
- ##### 지하철 구간 등록 인수 테스트 작성과 기능 구현
  - 역 사이에 새로운 역을 등록할 경우
    - 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
  
  - 새로운 역을 상행 종점으로 등록할 경우
  
  - 새로운 역을 하행 종점으로 등록할 경우
    
- ##### 구간 등록 시 예외 케이스를 고려하기
  - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
  - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    - 아래의 이미지 에서 A-B, B-C 구간이 등록된 상황에서 B-C 구간을 등록할 수 없음(A-C 구간도 등록할 수 없음)
  - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

--- 

## 4단계(step4) - 구간 제거 기능

#### 요구사항 - 지하철 구간 제거 기능을 구현하기
- [x] 노선의 구간을 제거하는 기능을 구현하기
- [x] 구간 삭제 시 예외 케이스를 고려하기

#### 요구사항 설명
- ##### 노선의 구간을 제거하는 기능을 구현하기
  - 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
  - 중간역이 제거될 경우 재배치를 함
    - 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
    - 거리는 두 구간의 거리의 합으로 정함

- ##### 구간 삭제 시 예외 케이스를 고려하기
  - 기능 설명을 참고하여 예외가 발생할 수 있는 경우를 검증할 수 있는 인수 테스트를 만들고 이를 성공 시키세요.
    - 예시) 노선에 등록되어있지 않은 역을 제거하려 한다.
  - 구간이 하나인 노선에서 마지막 구간을 제거할 때
    - 제거할 수 없음