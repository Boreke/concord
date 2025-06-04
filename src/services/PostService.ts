import { Post } from "../class/Post";
import { env } from "../constants/env";
import { UserService } from "./UserService";

export class PostService {
    static posts: Post[] = [];
    constructor() {}

    static async fetchPosts(token: string) {
        const response = await fetch(`${env.API_URL}/posts`,{
            headers: {
                'Authorization': `Bearer ${token}`,
            }
        });
        if (!response.ok) {
            console.log(`Failed to fetch posts: ${response.statusText}`);
        } else {
            PostService.posts = await response.json();
            return PostService.posts;
        }
    }

    static async createPost(post: Post, token: string) {
        const response = await fetch(`${env.API_URL}/posts`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(post),
        });
        if (!response.ok) {
            throw new Error('Failed to create post');
        }
        const newPostJson = await response.json();

        PostService.posts.push(Post.fromAPI(newPostJson));
    }

    static async likePost(postId: number, token: string) {
        const response = await fetch(`${env.API_URL}/posts/${postId}/like`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to like post');
        }
        const likedPost = PostService.posts.find(post => post.id === postId);

        if (likedPost) {
            likedPost.likeCount += 1;
        }
    }

    static async unlikePost(postId: number, token: string) {
        const response = await fetch(`${env.API_URL}/posts/${postId}/unlike`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to unlike post');
        }
        const unlikedPost = PostService.posts.find(post => post.id === postId);
        if (unlikedPost) {
            unlikedPost.likeCount -= 1;
        }
    }

    static async fetchRecommendedPosts(userId: number | undefined, token: string) {
        const response = await fetch(`${env.API_URL}/posts/${userId}/recommended-posts`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            console.log(`Failed to fetch recommended posts: ${response.statusText}`);
            
            throw new Error('Failed to fetch recommended posts: ' + response.statusText);
        }
        const recommendedPosts = await response.json();
        return recommendedPosts.map((post: any) => Post.fromAPI(post));
    }

    static async fetchPostsByUserId(userId: number, token: string) {
        const response = await fetch(`${env.API_URL}/posts/user/${userId}`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        console.log(response);
        
        if (!response.ok) {
            console.log(`Failed to fetch posts by user ID ${userId}: ${response.statusText}`);
            throw new Error('Failed to fetch posts by user ID: ' + response.statusText);
        }
        const posts = await response.json();
        return posts.map((post: any) => Post.fromAPI(post));
    }
}