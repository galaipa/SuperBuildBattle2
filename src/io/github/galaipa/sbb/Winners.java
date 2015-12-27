package io.github.galaipa.sbb;


public class Winners implements Comparable<Winners> {
    Jokalaria team;
    int score;

    public Winners(Jokalaria name, int score) {
        this.team = name;
        this.score = score;
    }

    public int compareTo(Winners user) {
        return user.getScore() - this.getScore();
    }

    public Jokalaria getName() {
        return team;
    }

    public int getScore() {
        return score;
    }

    public String toString() {
        return "Name: " + team + " Score: " + score;
    }
}
