package kimjb.japanese_voca.controller;

import kimjb.japanese_voca.dto.InsertWordRequestDto;
import kimjb.japanese_voca.dto.JLPTWordResponseDto;
import kimjb.japanese_voca.dto.WordResponse;
import kimjb.japanese_voca.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/word")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WordController {

    private final WordService wordService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        wordService.importWordsFromCsv(file);
        return ResponseEntity.ok("파일이 성공적으로 업로드되었습니다.");
    }

    @PostMapping("/upload/example")
    public ResponseEntity<String> uploadExampleCsv(@RequestParam("file") MultipartFile file) {
        wordService.uploadExampleCsv(file);
        return ResponseEntity.ok("예시 파일 업로드 성공");
    }

    @GetMapping("/all")
    public List<JLPTWordResponseDto> getWordAll() {
        return wordService.getWordAll();
    }

    @GetMapping("/chapter/{level}")
    public Map<String, Object> getChapterByLevel(@PathVariable String level) {
        return wordService.getChapterByLevel(level);
    }

    // 단어 의미 일렬로 배열
    @GetMapping("/jlpt/{chapter}/{level}")
    public List<JLPTWordResponseDto> getJLPTWord(@PathVariable("chapter") String chapter,
                                                 @PathVariable("level") String level) {
        return wordService.getJLPTWord(chapter, level);
    }

    @GetMapping("/search")
    public List<JLPTWordResponseDto> getSearchWord(@RequestParam(name = "searchText") String searchText) {
        return wordService.getSearchWord(searchText);
    }

    @GetMapping("/today")
    public List<JLPTWordResponseDto> getTodayWord() {
        return wordService.getTodayWord();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsertWordRequestDto> getWordById(@PathVariable Long id) {
        InsertWordRequestDto dto = wordService.getWordById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{wordId}")
    public ResponseEntity<Void> saveMeanings(@PathVariable Long wordId, @RequestBody InsertWordRequestDto wordDto) {
        wordService.updateWord(wordId, wordDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin")
    public Page<WordResponse> getAdminWordList(Pageable pageable) {
        return wordService.getAdminWordList(pageable);
    }
}
