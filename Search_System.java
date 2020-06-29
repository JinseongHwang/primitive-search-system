import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;

class WordInfo { // 각 단어 정보의 틀을 잡아주는 클래스
	String key; // 단어 그 자체
	int value; // 발견 빈도수
	// 각 파일 별로 파일이름, 빈도수
	Map<String, Integer> discoverSpot = new HashMap<String, Integer>();
}

// 정렬을 위해 필요한 함수와 메서드
class WordInfoComparator implements Comparator<WordInfo> {
	@Override
	public int compare(WordInfo o1, WordInfo o2) {
		String s1 = o1.key;
		String s2 = o2.key;
		return s1.compareToIgnoreCase(s2);
	}
}

public class Search_System {
	
	public static void main(String[] args) throws Exception{
		File file = new File("C:\\AAA_INDEX\\index.txt"); 
		File startPath = new File("C:\\AAA");
		Scanner systemScanner = new Scanner(System.in);
		
		int input = 0; // 입력 번호
		boolean alreadySelected = false; // 내부에서 동작 수행이 결정된 경우 입력받지 않기 위해 존재
		while(true) {
			if(!alreadySelected) { // 내부에서 결정되지 않았다면 입력받음
				System.out.println("=====================================================");
				System.out.println("안녕하세요. 검색 시스템에 오신 것을 환영합니다 ^_^");
				System.out.println("1: 인덱싱단계  2: 질의문입력  3: 종료");
				System.out.print("==> ");
				try {
					input = systemScanner.nextInt();
				} catch (InputMismatchException e) {
					System.out.println("잘못 입력하셨습니다. 1~3의 정수만 입력해주세요!");
					systemScanner.nextLine(); // 버퍼 비워주기
					continue;
				}
				
			}
			if(input == 3) {
				System.out.println("시스템을 종료하겠습니다 ^_^");
				break;
			}
			if(input == 2) {
				if(!file.exists()) { // index.txt가 존재하지 않는 경우
					System.out.println("인덱싱을 먼저 수행해주세요. 인덱싱을 시작할까요? (y/n)");
					String inp = systemScanner.next();
					if(inp.equals("y") || inp.equals("Y")) {
						System.out.println("인덱싱을 먼저 수행하겠습니다.");
						input = 1; alreadySelected = true;
						continue;
					} else if(inp.equals("n") || inp.equals("N")) {
						System.out.println("인덱싱을 하지 않고 질의문을 입력할 수 없습니다 ㅠㅠ");
						continue;
					} else {
						System.out.println("잘못 입력하셨습니다 ㅠㅠ");
						continue;
					}
				} else { // index.txt가 존재하는 경우(인덱싱 완료된 경우)
					// 탐색 + snippet
					boolean found = false; // 질의문을 찾지 못했을 경우 구분하기 위해 존재
					System.out.print("질의문을 입력해주세요 ==> ");
					String query = systemScanner.next(); // 질의문을 입력 받음
					Scanner sc = new Scanner(new FileReader(file));
					while(sc.hasNextLine()) {
						String str = sc.nextLine();
						String [] splstr = str.split(" "); // 입력된 문자열을 공백 단위로 쪼갬
						
						if(splstr[0].equals(query)) { // 0번째에는 항상 단어 그 자체가 존재
							found = true;
							System.out.println("'" + query + "'" + "의 검색 결과 " + splstr[1] + "건의 문서가 검색되었습니다.");
							
							// 파일 명을 저장하는 데이터의 index number는 2이상의 짝수이다.
							// 그 파일에서 단어가 출현한 빈도의 index number는 2이상의 홀수이다.
							for(int i = 2; i < splstr.length; i += 2) {
								System.out.println("[" + i / 2 + "] " + splstr[i] + " " + splstr[i + 1] + "회 출현");
								// snippet: 출현한 단어가 존재하는 줄을 한 줄 보여준다.
								String currPath = startPath + "\\" + splstr[i]; // 단어가 위치하는 파일의 경로
								sc = new Scanner(new FileReader(currPath));
								while(sc.hasNextLine()) { // 
									String currLine = sc.nextLine();
									if(currLine.contains(query)) { // 단어가 포함된 경우
										System.out.println(" >> " + currLine); // 그 줄을 출력
									}
								}
							}
						} else {
							continue;
						}
					}
					if(!found) { // 단어가 존재하지 않는 경우
						System.out.println("'" + query + "'" + "의 검색 결과 0건의 문서가 검색되었습니다.");
					}				
				}
			}
			if(input == 1) { // 인덱싱 단계
				if (file.exists()) { // 파일이 존재하는 경우, 이미 인덱싱 되었다고 볼 수 있다.
					System.out.println("이미 인덱싱 완료되었습니다!");
					continue;
				} else { // 파일이 존재하지 않는다면 인덱싱을 해주자.
					FileWriter fw = new FileWriter(file);
					String [] fileNames = startPath.list(); // 파일의 이름을 저장한다.
					
					// 단어의 추가 여부를 빠르게 판단하기 위해 TreeSet구조에 추가된 이름을 저장
					TreeSet<String> wordsTreeSet = new TreeSet<String>();
					// WordInfo 클래스의 객체 타입을 저장하는 ArrayList이다
					// 실질적으로 단어의 데이터를 모두 저장한다.
					ArrayList<WordInfo> wordsArrayList = new ArrayList<WordInfo>();
					
					for(int i = 0; i < fileNames.length; ++i) { // 파일 마다 반복하며 탐색
						Scanner sc = new Scanner(new FileReader(startPath.toString() + "\\" + fileNames[i]));
						
						while(sc.hasNext()) { // 파일 안에 단어를 반복하며 탐색
							String currKey = sc.next(); // 현재 단어를 반환
							if(wordsTreeSet.contains(currKey)) { // 이미 출현했던 단어라면
								for(int j = 0; j < wordsArrayList.size(); ++j) {
									if(wordsArrayList.get(j).key.equals(currKey)) {
										wordsArrayList.get(j).value++; // 발견된 단어의 개수를 증가시킴
										if(wordsArrayList.get(j).discoverSpot.containsKey(fileNames[i])) { // 한 파일에서 중복 발견 시
											int preValue = wordsArrayList.get(j).discoverSpot.get(fileNames[i]);
											wordsArrayList.get(j).discoverSpot.put(fileNames[i], preValue + 1);
										}
										else { // 새로운 파일에서 발견 시
											wordsArrayList.get(j).discoverSpot.put(fileNames[i], 1);
										}
										break;
									}
								}
							}
							else { // 처음 출현한 단어라면
								WordInfo currWord = new WordInfo(); // 객체 생성
								wordsTreeSet.add(currKey); // 빠른 탐색을 위해 TreeSet에 단어 추가
								currWord.key = currKey; // 객체 -> 키 입력
								currWord.value = 1; // 객체 -> 빈도수 1 입력
								currWord.discoverSpot.put(fileNames[i], 1); // 객체 -> 새로운 파일에서 1번 출현 입력
								wordsArrayList.add(currWord); // 생성 후 값 대입이 끝난 객체를 ArrayList에 추가
							}
						}
					}
					
					// wordsArrayList를 Key 값을 기준으로 정렬
					Collections.sort(wordsArrayList, new WordInfoComparator());
					
					// wordsArrayList의 각 요소들의 discoverSpot을 Value 기준 내림차순 정렬
					for(var v : wordsArrayList) {
						List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(v.discoverSpot.entrySet());
						Collections.sort(list, new Comparator<Entry<String, Integer>>() {
							public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
								return o2.getValue().compareTo(o1.getValue());				
							}
						});
						
						// index.txt에 입력하는 과정
						String kv = v.key + " " + v.value + " ";
						fw.write(kv);
						for(Entry<String, Integer> entry : list) {
							String freq = entry.getKey() + " " + entry.getValue() + " ";
							fw.write(freq);
						}
						fw.write("\n");
					}
					System.out.println("인덱싱을 완료했습니다!");
					alreadySelected = false;
					fw.close();
				}
			} // indexing
			if(input != 1 && input != 2 && input != 3) {
				System.out.println("잘못 입력하셨습니다. 1~3의 정수만 입력해주세요!");
			}
		} // while
		
	} // main

}
