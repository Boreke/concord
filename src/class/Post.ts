export class Post{
    id: number;
    userId: number;
    title: string;
    content: string;
    createdAt: string;
    likeCount: number;
    likedByCurrentUser: boolean;
    constructor(id: number, userId: number, title: string, content: string, createdAt: string, likeCount: number, likedByCurrentUser: boolean) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.likedByCurrentUser = likedByCurrentUser || false;
    }

    toString(): string {
        return `Post by User ${this.userId}: ${this.content}`;
    }

    static fromAPI(response: any): Post {
        return new Post(
            response.id,
            response.userId,
            response.title,
            response.content,
            response.createdAt,
            response.likeCount,
            response.likedByCurrentUser ? true : false // Ensure boolean value
        )
    }

}