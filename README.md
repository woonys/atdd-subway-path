# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 1단계 - 구간 추가 요구사항 반영

### 인수 조건
1. 구간 추가 관련 인수 조건

    ```text
   given 특정 노선에 구간이 1개 이상 등록되었을 때
   when 기존 구간 중 특정 구간의 상행역과 등록하려는 구간의 상행역이 같고 
        등록하려는 구간의 길이가 특정 구간의 길이보다 짧으면
   then 기존 구간 사이에 새 구간이 등록된다.
   ```

    ```text
   given 특정 노선에 구간이 1개 이상 등록되었을 때
   when 새 역을 상행종점역으로 등록하면 
   then 해당 노선의 상행종점역이 변경된다.
   ```

   ```text
   given 특정 노선에 구간이 1개 이상 등록되었을 때
   when 등록하려는 역이 기존 노선에 있다면
   then 예외를 반환한다.
   ```

2. 구간 제거 관련 인수 조건

    ```text
   given 특정 노선에 구간이 2개 이상 등록되었을 때
   when 노선에 등록된 역 중 가운데 역을 제거하면
   then 해당 역이 노선에서 제거되고
        전후 구간이 하나의 구간으로 합쳐진다.
   ```

    ```text
   given 특정 노선에 구간이 2개 이상 등록되었을 때
   when 노선의 상행종점역을 제거하면 
   then 해당 노선의 상행종점역이 제거되고
        그 다음 역이 전체 노선의 상행종점역이 된다.
   ```