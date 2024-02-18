import java.util.*;
import java.io.*;

public class Main {

    private static int n;
    private static int p;
    private static int c;
    private static int d;
    private static int rr;
    private static int rc;
    private static int[][] board;

    private static final int[] rdr = new int[]{-1, -1, 0, 1, 1, 1, 0, -1};
    private static final int[] rdc = new int[]{0, 1, 1, 1, 0, -1, -1, -1};
    private static final int[] sdr = new int[]{-1, 0, 1, 0};
    private static final int[] sdc = new int[]{0, 1, 0, -1};

    public static void main(String[] args) throws IOException{
        // n, m, p, c, d 입력됨 (3-50) (1-1000), (1-30), (1-n) (1-n)
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine(), " ");

        n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        p = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken());
        d = Integer.parseInt(st.nextToken());
        
        // int[n+1][n+1] board 생성
        board = new int[n+1][n+1];

        // rr, rc 입력 (1-n)
        st = new StringTokenizer(br.readLine() , " ");
        rr = Integer.parseInt(st.nextToken());
        rc = Integer.parseInt(st.nextToken());
        board[rr][rc] = -1;

        // 산타 sn, sr, sc 입력
        int[][] santas = new int[p+1][2];
        for (int i = 0; i < p; i++){
            st = new StringTokenizer(br.readLine(), " ");
            int n = Integer.parseInt(st.nextToken());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            santas[n][0] = r;
            santas[n][1] = c;
            board[r][c] = n;
        }

        // int[p+1] scores
        int[] scores = new int[p+1];       

        // int[p+1] pause 생성
        int[] pause = new int[p+1];

        // boolean[p+1] outs 생성
        boolean[] outs = new boolean[p+1];

        // 턴 만큼 반복 O(m)
        for (int x = 0; x < m; x++){
            // 각 거리 계산 O(n*n), 작거나 같으면 바꾸기 (i, j)
            int minD = Integer.MAX_VALUE;
            int minR = 0, minC = 0;
            for (int i = 1; i <= p; i++){
                if (!outs[i]){
                    int distance = (int)Math.pow(santas[i][0]-rr, 2) + (int)Math.pow(santas[i][1]-rc, 2);
                    if (distance <= minD){
                        minD = distance;
                        if (santas[i][0] > minR || (santas[i][0] == minR && santas[i][1] > minC)){
                            minR = santas[i][0];
                            minC = santas[i][1];
                        }
                    }
                }
            }

            // 거리가 그대로면 산타 없다는 뜻이므로 종료
            if (minD == Integer.MAX_VALUE){
                break;
            }

            // 루돌프 이동
            board[rr][rc] = 0;
            int direc = move(rr, rc, minR, minC);
            rr += rdr[direc];
            rc += rdc[direc];

            // 충돌 확인
            if (board[rr][rc] != 0) {
                int sn = board[rr][rc];

                // c 획득
                scores[sn] += c;
            
                // pause = 2
                pause[sn] = 2;
                
                // c 밀림
                board[rr][rc] = -1;
                int mr = rr + rdr[direc] * c;
                int mc = rc + rdc[direc] * c;
                 
                while (!isOut(mr, mc)){
                    if (board[mr][mc] != 0){
                        // 상호작용
                        int temp = board[mr][mc];
                        board[mr][mc] = sn;
                        santas[sn][0] = mr;
                        santas[sn][1] = mc;
                        sn = temp;

                        mr += rdr[direc];
                        mc += rdr[direc];
                    }else{
                        board[mr][mc] = sn;
                        santas[sn][0] = mr;
                        santas[sn][1] = mc;
                        break;
                    }
                }

                if (isOut(mr, mc)){
                    outs[sn] = true;
                }
                // 탈락 여부 확인, 탈락이면 outs true (반)
                //  상호작용 확인, temp 에 담으며 다시 위에 과정 반복
            }else{
                board[rr][rc] = -1;
            }    

            // 산타 이동 O(p), pause = 0인, 방향 확인 -> 이동
            for (int i = 1; i <= p; i++){
                if (!outs[i] && pause[i] == 0){
                    int sr = santas[i][0];
                    int sc = santas[i][1];
                    
                    direc = move(sr, sc, rr, rc);

                    if (direc % 2 == 0){
                        int mr = sr + sdr[direc/2];
                        int mc = sc + sdc[direc/2];
                        if (board[mr][mc] == 0){
                            // 이동
                            board[sr][sc] = 0;
                            board[mr][mc] = i;
                            santas[i][0] = mr;
                            santas[i][1] = mc;
                        }else if(board[mr][mc] == -1){
                            // 충돌
                            int sn = i;

                            // d 획득
                            scores[i] += d;
                            
                            // pause = 2
                            pause[i] = 2;

                            // d 밀림
                            direc = (direc/2 + 2) % 4;
                            board[sr][sc] = 0;
                            mr += sdr[direc] * d;
                            mc += sdc[direc] * d;

                            // 탈락 여부 확인, 탈락이면 outs true (반)
                            //  상호작용 확인, temp 에 담으며 다시 위에 과정 반

                            while (!isOut(mr, mc)){
                                if (board[mr][mc] != 0){
                                    // 상호작용
                                    int temp = board[mr][mc];
                                    board[mr][mc] = sn;
                                    santas[sn][0] = mr;
                                    santas[sn][1] = mc;
                                    sn = temp;

                                    mr += sdr[direc];
                                    mc += sdc[direc];
                                }else{
                                    board[mr][mc] = sn;
                                    santas[sn][0] = mr;
                                    santas[sn][1] = mc;
                                    break;
                                }
                            }         

                            if (isOut(mr, mc)){
                                outs[sn] = true;
                            }
                            
                        }
                        // 산타면 이동 불가
                    } else{
                        int mr = sr + sdr[(direc + 7)/2 % 4];
                        int mc = sc + sdc[(direc + 7)/2 % 4];
                        if (board[mr][mc] == 0){
                            // 이동
                            board[sr][sc] = 0;
                            board[mr][mc] = i;
                            santas[i][0] = mr;
                            santas[i][1] = mc;
                        }else if (board[mr][mc] > 0){
                            mr = sr + sdr[(direc+1)/2 % 4];
                            mc = sc + sdc[(direc+1)/2 % 4];
                            if (board[mr][mc] == 0){
                                // 이동
                                board[sr][sc] = 0;
                                board[mr][mc] = i;
                                santas[i][0] = mr;
                                santas[i][1] = mc;
                            }
                            // 둘다 산타면 못움직임
                        }
                    }
                }
            }
                

            // pause == 0 아닌 애들--
            // outs false 인애들 점수 ++
            for (int i = 1; i <= p; i++){
                if (pause[i] > 0){
                    pause[i]--;
                }
                if (!outs[i]){
                    scores[i]++;
                }
            }
        }

        // scores 출력
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= p; i++){
            result.append(scores[i]);
            result.append(" ");
        }
        System.out.println(result.toString().trim());
    }

    private static int move(int startr, int startc, int destr, int destc){
        int direc = 0;
        if (startr < destr){
            if (startc < destc){
                direc = 3;
            }else if(startc > destc){
                direc = 5;
            }else{
                direc = 4;
            }
        }else if(startr > destr){
            if (startc < destc){
                direc = 1;
            }else if(startc > destc){
                direc = 7;
            }else{
                direc = 0;
            }
        }else{
            if (startc < destc){
                direc = 2;
            }else if(startc > destc){
                direc = 6;
            }
        }
        return direc;
    }

    private static boolean isOut(int r, int c){
        if (r > 0 && r <= n && c > 0 && c <= n){
            return false;
        }
        return true;
    }
}

// 요약
// p 명의 산타, nxn 격자, (1,1) 시작
// m턴에 걸쳐 루돌프 -> 산타 움직임(1-p)
// 기절이나 격자 밖에 나간 산타는 못움직임
// 둘의 거리는 진짜 거리에 루트 안씌운거

// 루돌프
// 탈락 안한 가장 가까운 산타를 향해 1칸 돌진, 같으면 r좌표가, 그것도 같으면 c좌표가 큰 산타에게 돌진
//8방향 중 한가지로 한칸 돌진

// 산타
// 1-p까지 순서대로, 루돌프에게 가까워지는 방향으로 1칸 이동
// 다른 산타가 있거나 게임판 밖으로는 못 움직임
// 4방향 움직임, 가까워지는 방향 여러개면 상우하좌순으로 움직임

// 충돌
// 루돌프가 움직여 충돌하면 산타는 c 점수, 산타는 ㄹ돌프가 이동해온 방향으로 c만큼 밀림
// 산타가 움직여 충돌하면, 산타는 d 점수, 산타는 자신이 이동한 반대방향으로 d만큼 밀림
// 밀려난게 게임판 밖이면 탈락, 다른산타가 있으면 상호작용

// 상호작용
// 충돌 후 착지 칸에 다른 산타 있으면 그 산타는 1칸 밀림
// 연쇄적으로 수행됨, 게임밖이면 탈락

// 기절
// 산타는 루돌프랑 충돌하면 기절함, k번쨰 턴이면 k+1 턴까지 기절, k+2부터 깸, 기절한상태에서도 돌진 가능

// m 턴 이후 게임 종료, 산타 모두 탈락하면 게임 종료, 턴이후 탈락 안한 산타들은 1점 추가부여
// 각 산타의 최종 점수 구하기

// O()