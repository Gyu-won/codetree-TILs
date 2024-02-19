import java.util.*;
import java.io.*;

public class Main {

    private static final int[] santaRowMove = new int[]{-1, 0, 1, 0};
    private static final int[] santaColMove = new int[]{0, 1, 0, -1};

    private static int n;
    private static boolean[] outs;
    private static int[][] santas;
    private static int[][] board;

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine(), " ");

        n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        int p = Integer.parseInt(st.nextToken());
        int c = Integer.parseInt(st.nextToken());
        int d = Integer.parseInt(st.nextToken());

        // int[n+1][n+1] board 선언
        board = new int[n+1][n+1];

        // 루돌프 위치를 입력받음, board에 표시
        st = new StringTokenizer(br.readLine(), " ");
        int rudolphRow = Integer.parseInt(st.nextToken());
        int rudolphCol = Integer.parseInt(st.nextToken());
        board[rudolphRow][rudolphCol] = -1;

        // int[p+1][2] santas 입력받기, board에 표시
        santas = new int[p+1][2];
        for (int i = 0; i < p; i++){
            st = new StringTokenizer(br.readLine(), " ");
            int santaNumber = Integer.parseInt(st.nextToken());
            int santaRow = Integer.parseInt(st.nextToken());
            int santaCol = Integer.parseInt(st.nextToken());

            santas[santaNumber][0] = santaRow;
            santas[santaNumber][1] = santaCol;
            board[santaRow][santaCol] = santaNumber;
        }

        // int[p+1] scores 선언
        int[] scores = new int[p+1];

        // boolean[p+1] outs 선언
        outs = new boolean[p+1];

        // int[p+1] stun 선언
        int[] stun = new int[p+1];

        // m 번 반복
        for (int round = 0; round < m; round++){

            // 루돌프 움직임 (거리 계산, 이동, 충돌 확인)
            
            // 움직일 산타 정하기 (minSantaRow, minSantaCol)
            int minDistance = Integer.MAX_VALUE;
            int minSantaRow = 0;
            int minSantaCol = 0;
            for (int santaNumber = 1; santaNumber <= p; santaNumber++){
                if (!outs[santaNumber]){
                    int[] santa = santas[santaNumber];
                    int santaRow = santa[0];
                    int santaCol = santa[1];

                    int distance = calculateDistance(rudolphRow, rudolphCol, santaRow, santaCol);

                    if (distance < minDistance){
                        minDistance = distance;
                        minSantaRow = santaRow;
                        minSantaCol = santaCol;
                    }else if(distance == minDistance){
                        if (minSantaRow < santaRow || (minSantaRow == santaRow && minSantaCol < santaCol)){
                            minSantaRow = santaRow;
                            minSantaCol = santaCol;
                        }
                    }
                }
            }

            // 방향 저장하기
            int rudolphRowMove = 0, rudolphColMove = 0;
            if (rudolphRow < minSantaRow){
                rudolphRowMove++;
            }else if (minSantaRow < rudolphRow){
                rudolphRowMove--;
            }
            if (rudolphCol < minSantaCol){
                rudolphColMove++;
            }else if (minSantaCol < rudolphCol){
                rudolphColMove--;
            }

            // 움직이기
            board[rudolphRow][rudolphCol] = 0;
            rudolphRow += rudolphRowMove;
            rudolphCol += rudolphColMove;
            
            // 충돌
            if (board[rudolphRow][rudolphCol] > 0){
                int santaNumber = board[rudolphRow][rudolphCol];

                // 산타 점수 더하기
                scores[santaNumber] += c;

                // 기절
                stun[santaNumber] = 2;

                // 해당 칸 바꾸기
                board[rudolphRow][rudolphCol] = -1;

                // 산타 밀림 (함수로)
                push(santaNumber, rudolphRow, rudolphCol, rudolphRowMove, rudolphColMove, c);
            }else{
                board[rudolphRow][rudolphCol] = -1;
            }

            // 산타 움직임 (거리 계산, 이동, 충돌 확인)
            for (int santaNumber = 1; santaNumber <= p; santaNumber++){
                if (!outs[santaNumber] && stun[santaNumber] == 0){
                    int santaRow = santas[santaNumber][0];
                    int santaCol = santas[santaNumber][1];

                    // 산타가 움직이는 방향 구하기
                    int santaDirection = calculateSantaDirection(santaRow, santaCol, rudolphRow, rudolphCol);
                    if (santaDirection == -1){
                        // 못움직이는 경우
                        continue;
                    }

                    // 산타 이동
                    board[santaRow][santaCol] = 0;
                    santaRow += santaRowMove[santaDirection];
                    santaCol += santaColMove[santaDirection];

                     // 충돌
                    if (board[santaRow][santaCol] == -1){
                        // 산타 점수 더하기
                        scores[santaNumber] += d;

                        // 기절
                        stun[santaNumber] = 2;

                        // 산타 밀림 (함수로)
                        santaDirection = (santaDirection + 2) % 4;
                        push(santaNumber, santaRow, santaCol, santaRowMove[santaDirection], santaColMove[santaDirection], d);
                    }
                    else{
                        board[santaRow][santaCol] = santaNumber;
                        santas[santaNumber][0] = santaRow;
                        santas[santaNumber][1] = santaCol;
                    }
                }
            }

            // 점수 더하기
            for (int santaNumber = 1; santaNumber <= p; santaNumber++){
                if (!outs[santaNumber]){
                    scores[santaNumber]++;
                }
                if (stun[santaNumber] > 0){
                    stun[santaNumber]--;
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (int santaNumber = 1; santaNumber <= p; santaNumber++){
            result.append(scores[santaNumber]);
            result.append(" ");
        }
        System.out.println(result.toString().trim());
    }

    private static int calculateDistance(int fromRow, int fromCol, int toRow, int toCol){
        return (int)Math.pow(fromRow - toRow, 2) + (int)Math.pow(fromCol - toCol, 2);
    }

    private static void push (int santaNumber, int currentRow, int currentCol, int rowMove, int colMove, int power){
        int movedRow = currentRow + rowMove * power;
        int movedCol = currentCol + colMove * power;

        if (isValidRowAndCol(movedRow, movedCol)){
            if (board[movedRow][movedCol] > 0){
                int movingSantaNumber = board[movedRow][movedCol];
                board[movedRow][movedCol] = santaNumber;
                santas[santaNumber][0] = movedRow;
                santas[santaNumber][1] = movedCol;
                push(movingSantaNumber, movedRow, movedCol, rowMove, colMove, 1);
            }else{
                board[movedRow][movedCol] = santaNumber;
                santas[santaNumber][0] = movedRow;
                santas[santaNumber][1] = movedCol;
            }
        }else{
            outs[santaNumber] = true;
        }
    }

    private static int calculateSantaDirection(int santaRow, int santaCol, int rudolphRow, int rudolphCol){
        List<Direction> directions = new ArrayList<>();

        int currentDistance = calculateDistance(santaRow, santaCol, rudolphRow, rudolphCol);
        for (int direction = 0; direction < 4; direction++){
            int movedRow = santaRow + santaRowMove[direction];
            int movedCol = santaCol + santaColMove[direction];
            
            if (isValidRowAndCol(movedRow, movedCol)){
                int movedDistance = calculateDistance(movedRow, movedCol, rudolphRow, rudolphCol);

                if (movedDistance < currentDistance){
                    directions.add(new Direction(movedDistance, direction, movedRow, movedCol));
                }
            }
        }

        Collections.sort(directions, new DirectionComparator());

        for (Direction direction: directions){
            if (board[direction.row][direction.col] <= 0){
                return direction.direction;
            }
        }
        return -1;
    }

    private static boolean isValidRowAndCol(int row, int col){
        return row > 0 && row <= n && col > 0 && col <= n;
    }

    static class Direction{
        private final int distance;
        private final int direction;
        private final int row;
        private final int col;

        private Direction(int distance, int direction, int row, int col){
            this.distance = distance;
            this.direction = direction;
            this.row = row;
            this.col = col;
        }
    }

    static class DirectionComparator implements Comparator<Direction>{
        @Override
        public int compare(Direction d1, Direction d2){
            if (d1.distance == d2.distance){
                return d1.direction - d2.direction;
            }
            return d1.distance - d2.distance;
        }
    }
}



// 요약
// nxn 격자, 좌상단 (1, 1)
// m 개의 턴, 루돌프 -> 산타 움직임 (1->p), 기절이나 탈락한 산타는 못 움직임, 산타 없으면 종료
// 거리는 제곱 합

// 루돌프 움직임
// 거리 계산하여 가장 가까운 산타에게 돌진, 2명 이상이면 r이 큰, c 가 큰 산타에게 돌진
// 8 방향 중 제일 가까워지는 방향으로 돌진

// 산타 움직임
// 기절, 탈락 안한 산타가 1->p 까지
// 가장 가까워지는 방향으로 이동, 막혀있으면 그다음으로 칸인데 그래도 가까워져야함
// 상하좌우고, 거리가 같다면 상우하좌 순으로

// 충돌
// 루돌프 -> 산타면 산타 c 점수, 산타 c 밀림
// 산타 -> 루돌프면 산타 p 점수, 산타, d 밀림
// 게임 밖이면 탈락, 산타 있으면 상호작용, 그냥 없으면 그 위치
// 충돌 후 기절함 (k+2 번째 턴부터 정상) 움직이지 못하는 거임

// 상호작용
// 산타 있으면 그 방향으로 연쇄적으로 1칸씩 밀림

// 매 라운드 마다 살아있는 산타는 1점씩 부여

// O(m * p * p)