package com.ltiex.EAS;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception{
		System.out.println("evolutionary algorithms, EAS");
		
	

		
		//生成初始种群
		LinkedList<City[]> answers = new LinkedList<City[]>();
		
		
		int initLen = 5000;
		int answerLen = 50;
		int xLen = 100;int yLen = 100;
		
		City citys[] = new City[answerLen];
//		int xArr[] = {79,78,6,9,81,33,76,2,5,13,92,23,27,62,16,44,71,92,7,57};
//		int yArr[] = {72,13,15,22,12,32,14,89,52,22,31,93,30,0,85,73,84,71,6,53};
//		Scanner scanner = new Scanner(System.in);
		for(int i = 0; i < citys.length;i++){
//			int x = scanner.nextInt();
//			int y = scanner.nextInt();
			int x = (int)(Math.random()*xLen);
			int y = (int)(Math.random()*yLen);
//			citys[i] = new City(xArr[i],yArr[i]);
			citys[i] = new City(x,y);/*保证生成的点不重复*/
			System.out.print(citys[i].toString() + "\n");
		}
		int generation = 0;
		long geneValue = calculate(citys);
		/* 当步长少于900时退出循环 */
		while(geneValue > 1000) {
//			System.out.println("std distance:" + Main.calculate(citys));

			
			for(int k = 0;k < initLen;k++) {
				City tempCitys[] = new City[answerLen];
				int x = (int)(Math.random()*(answerLen-1));
				for(int i = 0; i < answerLen;i++) {
					int tempIndex = x%(answerLen);
					tempCitys[tempIndex] = new City(citys[i].getX(), citys[i].getY());
					x++;
				}
				answers.add(tempCitys);
			}
//			Main.showInfo(answers);
			
			LinkedList<Long> answersFitness = Main.getFitnesses(answers);
//			for(int i = 0;i < answersFitness.size();i++) {
//				System.out.println("distance:" + answersFitness.get(i));
//			}
			
			while(answers.size() > 1) {
				generation++;
				
				/* 碱基置换 */
				double mutationRate = 3E-3;
				for(int i = 0;i < answers.size();i++){
					boolean isMutate = Math.random() <= mutationRate;
					for(int times = 0;times < answerLen && isMutate;times++) {
						int a = (int)(Math.random()*(answerLen-1));
						int b = (int)(Math.random()*(answerLen-1));
						City tempAnswer[] = answers.get(i);
						City tempCity = tempAnswer[a];
						tempAnswer[a] = tempAnswer[b];
						tempAnswer[b] = tempCity; 
					}
				}
				
				/* 基因倒置 */
				int power = 2;
				for(int i = 0;i < answers.size()>>power;i++){
					City aCitys[] = answers.get(i);
					City newCitys[] = new  City[answerLen];
					System.arraycopy(aCitys, 0, newCitys, 0, answerLen);
					
					int aPoint = (int)(Math.random()*(answerLen-1));
					int bPoint = (int)(Math.random()*(answerLen-1));
					aPoint >>= 1;bPoint >>= 1;
					int delta = Math.abs(aPoint - bPoint);
					System.arraycopy(aCitys, aPoint, newCitys, bPoint, delta);
					System.arraycopy(aCitys, bPoint, newCitys, aPoint, delta);
					
					answers.add(newCitys);
				}
				
				answersFitness = Main.getFitnesses(answers);
				
				
				/* 淘汰 */
				double rate = 0.9;
				
				long fitnesses[] = new long[answersFitness.size()];
				for(int i = 0;i < fitnesses.length;i++) {
					fitnesses[i] = answersFitness.get(i);
				}
				Arrays.sort(fitnesses);
				int num = (int) (fitnesses.length*rate)+1;
				long targetMax = fitnesses[fitnesses.length-num];/* 确保不会把所有的生命体淘汰 */
				
				for(int i = 0;i < answers.size();i++) {
					if(isRepeat(answers.get(i), xLen,yLen) || answersFitness.get(i) >= targetMax && answers.size() > 1) {
						answers.remove(i);
						answersFitness.remove(i);
						i--;
					}
				}
				
				answersFitness = Main.getFitnesses(answers);
//				System.out.printf("第%d代,size:%d\n",generation,answers.size());

			}
			
			citys = answers.getFirst();
			geneValue = calculate(citys);
			if(generation % 200 == 0) {
				System.out.printf("第%d代 value:%d size:%d\n",generation,geneValue,answers.size());
			}
			

		}
		
		
		System.out.println("done");
//		Main.showInfo(answers);
		System.out.println("res x,y");
		for(int x=0;x < answerLen;x++) {
			System.out.print(citys[x].toString() + "\n");
		}
		System.out.println("distance:" + calculate(citys));
		
	}
	
	
	private static boolean isRepeat(City citys[],int xLen,int yLen){
		boolean res = false;
		/* x y value*/
		int map[][] = new int[yLen][xLen];
		for(int y = 0;y < yLen;y++) {
			for(int x = 0;x < xLen;x++) {
				map[y][x]++;
			}
		}
		outer:for(int y = 0;y < yLen;y++) {
			for(int x = 0;x < xLen;x++) {
				res = map[y][x]>1;
				if(res) break outer;
			}
		}
		return res;
	}
	
	private static long calculate(City citys[]) {
		long distance = 0L;
		int x = 0;int y = 0;
		for(int i = 0;i < citys.length - 1;i++) {
			x = Main.getDistance(citys[i].getX(),citys[i+1].getX());
			y = Main.getDistance(citys[i].getY(),citys[i+1].getY());
			
			distance += x+y;
		}
		x = Main.getDistance(citys[0].getX(),citys[citys.length-1].getX());
		y = Main.getDistance(citys[0].getY(),citys[citys.length-1].getY());
		distance += x+y;
		return distance;
	}
	
	private static int getDistance(int x1,int x2) {
		return (Math.abs(x1-x2));
	}
	
	private static LinkedList<Long> getFitnesses(LinkedList<City[]> answers){
		LinkedList<Long> answersFitness = new LinkedList<Long>();
		for(int k = 0;k < answers.size();k++) {
			answersFitness.add(new Long(Main.calculate(answers.get(k))));
		}
		return answersFitness;
	}
	
	private static void showInfo(LinkedList<City[]> answers) {
		for(int y = 0;y < answers.size();y++) {
			City citys[] = answers.get(y);
			for(int x = 0;x < citys.length;x++) {
				System.out.print(citys[x].toString() + " ");
			}
			System.out.println();
		}
	}
}
