package com.springles.domain.constants;

public interface BaseEnumCode<T> {

    /** 향후 클라이언트 코드에서 key-value형식으로 Enum을 보는 과정이 필요할 수도 있을 것 같아
     * 형식 변환과 Enum을 검증하는 역할로 사용하려고 만들어놓은 코드입니다.
     * 다만 아직 클라이언트 코드 작성 전이라 필요성이 확실하지는 않습니다!
     * **/
    T getValue();
}
