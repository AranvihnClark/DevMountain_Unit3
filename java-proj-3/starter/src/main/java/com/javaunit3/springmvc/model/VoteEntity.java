package com.javaunit3.springmvc.model;

import javax.persistence.*;

@Entity
@Table(name = "votes")
public class VoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voteId;

    @Column(name = "voter_name")
    private String voterName;

    public Integer getVoteId() {
        return this.voteId;
    }

    public void setVoteId(Integer voteId) {
        this.voteId = voteId;
    }

    public String getVoterName() {
        return this.voterName;
    }

    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }
}
