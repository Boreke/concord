export class Follow {
    followerId: number;
    followeeId: number;

    constructor(followerId: number, followeeId: number) {
        this.followerId = followerId;
        this.followeeId = followeeId;
    }
    getFollowerId(): number {
        return this.followerId;
    }

    getFolloweeId(): number {
        return this.followeeId;
    }

    toString(): string {
        return `Follow: ${this.followerId} -> ${this.followeeId}`;
    }
    static fromAPI(response:any): Follow {
        return new Follow(
            response.followerId,
            response.followeeId
        )
    }
}