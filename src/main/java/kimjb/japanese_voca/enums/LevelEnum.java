package kimjb.japanese_voca.enums;

import lombok.Getter;

@Getter
public enum LevelEnum {
    N5("N5")
    ,N4("N4")
    ,N3("N3")
    ,N2("N2")
    ,N1("N1")
    ,ALL("전체")
    ;

    private String value;

    LevelEnum(String value) {
        this.value = value;
    }

    public String getKey() {
        return name();
    }

    public String getValue() {
        return value;
    }
}
