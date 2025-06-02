export class Follow {
    constructor(followerId, followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
    }
    getFollowerId() {
        return this.followerId;
    }

    getFolloweeId() {
        return this.followeeId;
    }

    toString() {
        return `Follow: ${this.followerId} -> ${this.followeeId}`;
    }
    static fromAPI(response){
        return new Follow(
            response.followerId,
            response.followeeId
        )
    }
}