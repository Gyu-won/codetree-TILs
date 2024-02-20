import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException{
        // l과 q를 입력받는다. (3-10억, 1-100,000)
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine(), " ");

        int l = Integer.parseInt(st.nextToken());
        int q = Integer.parseInt(st.nextToken());

        // command 입력받기
        Deque<Making> makings = new ArrayDeque<>();
        Deque<Entrance> entrances = new ArrayDeque<>();
        Deque<Integer> pictures = new ArrayDeque<>();
        int t = 0;

        for (int i = 0; i < q; i++){
            st = new StringTokenizer(br.readLine(), " ");
            String command = st.nextToken();
            t = Integer.parseInt(st.nextToken());
            if (command.equals("100")){
                int x = Integer.parseInt(st.nextToken());
                String name = st.nextToken();
                makings.offer(new Making(t, x, name));
            }else if(command.equals("200")){
                int x = Integer.parseInt(st.nextToken());
                String name = st.nextToken();
                int n = Integer.parseInt(st.nextToken());
                entrances.offer(new Entrance(t, x, name, n));
            }else{
                pictures.offer(t);
            }
        }

        List<String>[] tables = new ArrayList[l];
        for (int i = 0; i < l; i++){
            tables[i] = new ArrayList<>();
        }
        
        int zeroLoc = 0;
        int[] counts = new int[2];
        List<Entrance> visitors = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for (int time = 1; time <= t; time++){
            zeroLoc = rotate(zeroLoc, l);
            
            // 초밥 올리기 Sushi를 time
            if (!makings.isEmpty() && makings.peek().t == time){
                addSushi(tables, zeroLoc, l, makings.poll());
                counts[1]++;
            }

            // 손님입장 
            if (!entrances.isEmpty() && entrances.peek().t == time){
                Entrance entrance = entrances.poll();
                visitors.add(entrance);
                counts[0]++;
            }

            // 먹기
            for (int i = 0; i < visitors.size(); i++){
                eat(zeroLoc, l, tables, i, counts, visitors);
            }

            // for(int i = zeroLoc; i < l; i++){
            //     System.out.println(tables[i].toString());
            // }
            // for (int i = 0; i < zeroLoc; i++){
            //     System.out.println(tables[i].toString());
            // }
            // System.out.println("--------------");

            // 사진촬영
            if (!pictures.isEmpty() && pictures.peek() == time){
                pictures.poll();
                result.append(counts[0]);
                result.append(" ");
                result.append(counts[1]);
                result.append("\n");
            }
        }

        System.out.println(result.toString().trim());        
    }

    private static void eat(int zeroLoc, int l, List<String>[] tables, int idx, int[] counts, List<Entrance> visitors){
        Entrance visitor = visitors.get(idx);
        int location = (zeroLoc + visitor.x) % l;

        List<String> table = tables[location];
        while (true) {
            if (visitor.n == 0){
                visitors.remove(visitor);
                counts[0]--;
                break;
            } else {
                if (table.remove(visitor.name)){
                    visitor.n--;
                    counts[1]--;
                }else{
                    break;
                }
            }
        }
    }

    private static void addSushi(List<String>[] tables, int zeroLoc, int l, Making making){
        int idx = (making.x + zeroLoc) % l;
        tables[idx].add(making.name);

        // for(int i = zeroLoc; i < l; i++){
        //     System.out.println(tables[i].toString());
        // }
        // for (int i = 0; i < zeroLoc; i++){
        //     System.out.println(tables[i].toString());
        // }
        // System.out.println("--------------");
    }

    private static int rotate(int zeroLoc, int l){
        if (zeroLoc == 0){
            zeroLoc = l-1;
        }else{
            zeroLoc--;
        }
        return zeroLoc;
    }

    static class Making{
        private final int t;
        private final int x;
        private final String name;

        Making(int t, int x, String name){
            this.t = t;
            this.x = x;
            this.name = name;
        }
    }

    static class Entrance{
        private final int t;
        private final int x;
        private final String name;
        private int n;

        Entrance(int t, int x, String name, int n){
            this.t = t;
            this.x = x;
            this.name = name;
            this.n = n;
        }
    }
}

// 요약
// L개의 의자, 초밥은 1초에 한칸씩 시계방향 회전

// 초밥 만들기
// t 시간에 x 앞에 벨트에 초밥 올림 with name, 같은 위치 여러개 가능

// 손님입장
// t에 x로 가서 앉는다, 이름 name, 이름 적힌거 n 개 먹고 나감

// 사진촬영
// 현재 오마케세 집에 사람 수와 초밥 수 출력

// t는 (1-10억)