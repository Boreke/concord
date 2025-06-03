export class Like {
    userId: number;
    postId: number;

    constructor(userId: number, postId: number) {
        this.userId = userId;
        this.postId = postId;
    }

    getUserId(): number {
        return this.userId;
    }

    getPostId(): number {
        return this.postId;
    }

    toString(): string {
        return `Like: ${this.userId} liked post ${this.postId}`;
    }

    static fromAPI(response: any): Like {
        return new Like(
            response.userId,
            response.postId
        );
    }
}