# 기업 강의 슬라이드 이미지 생성 테마

> GPT 이미지 생성 시 이 파일을 함께 제공하여 일관된 슬라이드 디자인을 유지하세요.  
> 슬라이드 내용은 `lecture*.md` 파일을 별도로 전달합니다.

---

## 레이아웃 규격

- 이미지 전체: **16:9 비율** (1920×1080 px 기준)
- 슬라이드 배치: **3×3 그리드** — 한 이미지에 9장
- 셀 간 여백: **없음**
- 셀 테두리: **없음**
- 각 셀: 전체를 균등 3분할하여 16:9 유지

---

## 레이아웃 그리드

- 여백은 단순한 빈칸이 아니라 품위와 가독성을 높이는 요소로 다룬다
- 정렬은 단정하게 유지하되 지나치게 기계적으로 보이지 않게 한다
- 여러 페이지 목록, 컨택트시트, 썸네일 그리드처럼 보이게 하지 않는다

---

## 데이터 비주얼라이제이션

- 표, 도식, 핵심 포인트는 질서 있게 정리한다
- 강조는 최소화하고 읽는 속도를 우선한다
- 스타일의 개성을 남기면서도 핵심이 빠르게 읽히도록 만든다
- 라벨, 제목, 수치의 관계가 즉시 읽히도록 배치한다

---

## 톤 & 보이스

- 기능적 · 통계적 · 명확함 · 플랫 · 보편적
- 화면 전체에 절제된 존재감과 세련됨을 부여한다
- 스타일이 주제보다 앞서 보이지 않도록 한다
- 과하게 튀지 않지만 기억에 남는 인상을 지향한다

---

## 디자인 스타일

- **전체 분위기**: 기업용 기술 교육 자료, 클린 라이트 테마, 군더더기 없는 레이아웃
- **색상**: GPT가 기업용 기술 프레젠테이션에 적합한 라이트 테마 컬러를 자율적으로 선택 (가능하면 라이트 테마 우선)

---

## 셀 내부 레이아웃

- 슬라이드 레이아웃 유형은 콘텐츠 성격에 맞게 GPT가 자율적으로 선택한다
- 코드가 있으면 코드 블록을 적극 활용하고, 비교가 필요하면 표나 도식을 사용하는 등 내용에 따라 최적의 형식을 판단한다
- 단, 가독성과 위계는 항상 유지한다

---

## 생성 금지 규칙

- 각 카드의 상단이나 본문에 'Part 1', 'Part 2', 'Chapter', '섹션' 같은 구조적 라벨(칩 형태)을 절대 생성하지 않는다
- 원문 자료에 없는 '몇 배 향상' 등의 근거 없는 수치를 임의로 지어내지 않는다
- '완벽', '완전', '최고', '혁신적' 등 주관적이거나 과장된 표현을 일절 사용하지 않는다 — 마케팅적 수식어 배제, 오직 사실(Fact) 기반의 객관적이고 담백한 어조
- '다음 강의 예고', '총 학습 정리(Wrap-up)', '맺음말', 'Q&A' 등 본론 외의 불필요한 요약·예고 카드는 절대 생성하지 않는다
- 슬라이드 번호는 크게 배치하지 않는다 — 표시하더라도 콘텐츠를 해치지 않는 수준의 극소적인 크기로만 허용한다

---

## GPT 이미지 생성 시스템 프롬프트

아래를 GPT에 그대로 붙여넣고, 이어서 `lecture*.md` 파일 내용을 첨부하세요.

```
Create a 16:9 image (1920×1080 px) composed of a 3×3 grid of 9 presentation slides.

Layout:
- Grid: 3 columns × 3 rows, each cell exactly 640×360 px
- Zero gap and zero border between all cells

Layout & Grid principles:
- Whitespace is treated as a design element that adds dignity and readability, not merely empty space
- Alignment is clean and structured, but not mechanically rigid
- The overall image should not resemble a contact sheet, thumbnail grid, or paginated list

Data visualization principles:
- Tables, diagrams, and key points are organized with clear visual hierarchy
- Emphasis is minimal — reading speed takes priority over decoration
- Labels, titles, and values are positioned so their relationships are immediately legible

Tone & Voice:
- Functional, statistical, clear, flat, universal
- Restrained presence and sophistication across the entire frame
- Style should never overshadow the subject matter
- Memorable without being loud

Design style:
- Theme: corporate tech training, clean light mode (prefer light theme)
- Choose a cohesive, professional light color palette suitable for B2B tech slides — white or off-white backgrounds, restrained use of color

Per slide layout:
- Layout type is chosen autonomously based on the content of each slide
- Use bullet lists, code blocks, comparison tables, diagrams, or any format that best serves readability and comprehension
- Visual hierarchy and legibility must always be maintained regardless of layout choice

Strict prohibitions:
- Do NOT add structural label chips such as "Part 1", "Part 2", "Chapter", or "Section" anywhere on any slide
- Do NOT fabricate statistics or metrics (e.g. "3x faster") that are not present in the source material
- Do NOT use subjective or marketing language: words like "perfect", "complete", "best", "innovative", "revolutionary" are strictly forbidden — use only factual, neutral, matter-of-fact language
- Do NOT generate filler slides such as "Next Lecture Preview", "Wrap-up", "Summary", "Q&A", or any closing/opening card outside the actual lecture content
- Do NOT display slide numbers prominently — if shown at all, they must be minimal in size and must not interfere with the content

Read the attached lecture file and render slides 1–9 (or the specified range) in order.
```
