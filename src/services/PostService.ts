import { env } from "../constants/env";

export class PostService {
    posts: any[];
    constructor() {
        this.posts = [];
    }

    async fetchPosts() {
        const response = await fetch(`${env.API_URL}/posts`);
        if (!response.ok) {
            throw new Error('Failed to fetch posts');
        }
        this.posts = await response.json();
        return this.posts;
    }

    async createPost(postData: any) {
        const response = await fetch(`${env.API_URL}/posts`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(postData),
        });
        if (!response.ok) {
            throw new Error('Failed to create post');
        }
        const newPost = await response.json();
        this.posts.push(newPost);
        return newPost;
    }

}