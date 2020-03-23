package entity;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            String str = sc.nextLine();
            if(str.length() <= 0 || str == null){
                System.out.println("错误");
            }
            StringBuilder sb = new StringBuilder();
            for(char c : str.toCharArray()){
                if(c >='0' || c<='9'){
                    sb.append(c);
                }else{
                    sb.append(" ");
                }
            }

            String[] strs = sb.toString().split("\\s+");
            int maxL = 0;
            for(int i=0; i<strs.length; i++){
                maxL = strs[i].length()>maxL ? strs[i].length():maxL;
            }
            for(int i=0; i<strs.length; i++){
                if(strs[i].length() == maxL){
                    System.out.print(strs[i]);
                }
            }
        }
    }
}
