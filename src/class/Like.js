export class Like {
    constructor(userId, postId) {
        this.userId = userId;
        this.postId = postId;
    }

    getUserId() {
        return this.userId;
    }

    getPostId() {
        return this.postId;
    }

    toString() {
        return `Like: ${this.userId} liked post ${this.postId}`;
    }

    static fromAPI(response) {
        return new Like(
            response.id,
            response.userId,
            response.postId
        );
    }
}