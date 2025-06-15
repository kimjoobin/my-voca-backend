package kimjb.japanese_voca.repository.custom;

import kimjb.japanese_voca.dto.JLPTWordResponseDto;

import java.util.List;
import java.util.Map;

public interface CustomWordRepository {

    List<JLPTWordResponseDto> getJPLTWord(String chapter, String level);

    Map<String, Object> getChapterByLevel(String level);

    List<JLPTWordResponseDto> getTodayWord();

    List<JLPTWordResponseDto> getSearchWord(String searchText);
}
