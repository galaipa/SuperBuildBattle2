package io.github.galaipa.sbb;


public class Winners implements Comparable<Winners> {
    ArenaPlayer team;
    int score;

    public Winners(ArenaPlayer name, int score) {
        this.team = name;
        this.score = score;
    }

    public int compareTo(Winners user) {
        return user.getScore() - this.getScore();
    }

    public ArenaPlayer getName() {
        return team;
    }

    public int getScore() {
        return score;
    }

    public String toString() {
        return "Name: " + team + " Score: " + score;
    }
}
