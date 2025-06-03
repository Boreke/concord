export class Post{
    id: number;
    userId: number;
    title: string;
    content: string;
    createdAt: string;
    constructor(id: number, userId: number, title: string, content: string, createdAt: string) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    getId(): number {
        return this.id;
    }

    getUserId(): number {
        return this.userId;
    }

    getContent(): string {
        return this.content;
    }

    getCreatedAt(): string {
        return this.createdAt;
    }

    setTitle(title: string) {
        this.title = title;
    }

    getTitle(): string {
        return this.title;
    }
    setContent(content: string) {
        this.content = content;
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
            response.createdAt
        )
    }

}