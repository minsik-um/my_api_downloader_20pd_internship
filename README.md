# My API Downloader
2020 공공데이터 청년인턴십 - API 품질진단 업무에서 `api checker` 프로그램의 기능을 똑같이 구현했습니다. 

### 대상
- API 품질 검증 업무를 수행하는 공공데이터 청년 인턴
- Java 가능자

### 필요성
1. `api checker`를 이용하여 본 업무를 하려면 가능한 input parameter를 일일이 수정해서 실행해야 함
2. 위의 수정횟수는 한 두번이 아니라 수십번, 수백번도 필요할 수 있음
3. 간단한 반복문 코딩으로 parameter 수정을 하면서 요청하면 손목 피로와 시간을 줄일 수 있음!

### 개발 기간
2020-11-15

### 플랫폼
Java Maven Project

### 사용방법
`MyApiDownloader.java`에 필요한 기능을 구현했습니다. `App.java`에서 해당 객체를 만들어 사용하면 됩니다.

1. Java 개발 환경을 각자 맞게 갖춥니다. Java 11 정도면 적당합니다.
2. Maven Project를 Import 합니다. Eclipse도 좋고 VSC도 좋습니다. 전 VSC에서 개발하고 업무를 수행했습니다.
3. `App.java`에 넣은 `example1`, `example2` 예시를 참고하여 각자 상황에 맞게 호출 로직을 짜서 실행합니다.