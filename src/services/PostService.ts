import { Post } from "../class/Post";
import { env } from "../constants/env";
import { UserService } from "./UserService";

export class PostService {
    static posts: Post[] = [];
    constructor() {}

    static async fetchPosts() {
        const response = await fetch(`${env.API_URL}/posts`);
        if (!response.ok) {
            throw new Error('Failed to fetch posts');
        }
        PostService.posts = await response.json();
        return PostService.posts;
    }

    static async createPost(post: Post) {
        const response = await fetch(`${env.API_URL}/posts`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(post),
        });
        if (!response.ok) {
            throw new Error('Failed to create post');
        }
        const newPostJson = await response.json();

        PostService.posts.push(Post.fromAPI(newPostJson));
    }

    static async likePost(postId: number) {
        const currentUser= UserService.currentUser;
        if (!currentUser) {
            throw new Error('Current user is not authenticated');
        }
        const response = await fetch(`${env.API_URL}/posts/${postId}/like`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({
                currentUserId: currentUser.id
            })
        });
        if (!response.ok) {
            throw new Error('Failed to like post');
        }
        const likedPost = PostService.posts.find(post => post.id === postId);

        if (likedPost) {
            likedPost.likeCount += 1;
        }
    }

    static async unlikePost(postId: number) {
        const currentUser = UserService.currentUser;
        if (!currentUser) {
            throw new Error('Current user is not authenticated');
        }
        const response = await fetch(`${env.API_URL}/posts/${postId}/unlike`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({
                currentUserId: currentUser.id
            })
        });
        if (!response.ok) {
            throw new Error('Failed to unlike post');
        }
        const unlikedPost = PostService.posts.find(post => post.id === postId);
        if (unlikedPost) {
            unlikedPost.likeCount -= 1;
        }
    }

}