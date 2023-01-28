package hr.fer.oprpp1;

/**
 * Definition of voting result containing name of the band and its votes.
 *
 * @author Filip Vucic
 */
public class Result {

    private final String name;

    private final int votes;

    public Result(String name, int votes) {
        this.name = name;
        this.votes = votes;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }
}
