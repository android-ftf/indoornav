package com.example.egor.firstapp;

import java.util.ArrayList;

/**
 * Created by egor on 14.7.17.
 */

public class Beacon
{
    private double x, y;
    private String name;
    private int txPower;
    private double dist = 0;
    private ArrayList<Integer> mid = new ArrayList<>();

    void setBeacon(double a, double b, String c, int d)
    {
        this.x = a;
        this.y = b;
        this.name = c;
        this.txPower = d;
    }

    void addMid(int power)
    {
        int summ = 0;

        if (mid.size() < 6)
        {
            mid.add(power);
        }
        else
        {
            mid.remove(0);
            mid.add(power);
        }

        for (int j = 0 ; j < mid.size(); j++)
        {
            summ += mid.get(j);
        }

        dist = Math.pow(10, ((float) summ / mid.size() - txPower) / ((float) -10 * 3.2));
    }

    double getX()
    {
        return x;
    }

    double getY()
    {
        return y;
    }

    String getName()
    {
        return name;
    }

    int getTxPower()
    {
        return txPower;
    }

    double getDist()
    {
        return dist;
    }
}
