import { User } from '../class/User.js';
import { env } from '../constants/env.js';
import { AuthService } from './AuthService.js';
export class UserService {
    static currentUser: User | null = null;
    static users: User[];
    constructor() {}

    static async fetchUsers(token: string) {
        const response = await fetch(`${env.API_URL}/users`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch users');
        }
        UserService.users = await response.json();
        return UserService.users;
    }

    static async fetchCurrentUser(token: string) {
        const response = await fetch(`${env.API_URL}/users/me`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch current user');
        }
        let currentUser = await response.json();
        
        UserService.currentUser = User.fromAPI(currentUser);
        return User.fromAPI(currentUser);
    }

    static async followUser(userToFollowId: number, token: string) {
        const response = await fetch(`${env.API_URL}/users/${userToFollowId}/follow`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to follow user');
        }
    }

    static async unfollowUser(userToUnfollowId: number, token: string) {

        const response = await fetch(`${env.API_URL}/users/${userToUnfollowId}/unfollow`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to unfollow user');
        }
    }

    static async fetchRecommendedUsers(id:Number,token: string) {
        const response = await fetch(`${env.API_URL}/users/${id}/recommendations`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch recommended users');
        }
        const data = await response.json();
        return data.map((user: any) => User.fromAPI(user));
    }

    static async fetchUserById(userId: number, token: string) {
        
        const response = await fetch(`${env.API_URL}/users/${userId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            console.error('Failed to fetch user by ID:', response.statusText);
            throw new Error('Failed to fetch user by ID');
        }
        const data = await response.json();
        return User.fromAPI(data);
    }

    static async fetchFollowers(userId: number, token: string) {
        const response = await fetch(`${env.API_URL}/users/${userId}/followers`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch followers');
        }
        const data = await response.json();
        return data.map((user: any) => User.fromAPI(user));
    }

    static async fetchFollowing(userId: number, token: string) {
        const response = await fetch(`${env.API_URL}/users/${userId}/following`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch following');
        }
        const data = await response.json();
        return data.map((user: any) => User.fromAPI(user));
    }

    static async isUserFollowedByCurrentUser(userId: number, token: string) {
        const response = await fetch(`${env.API_URL}/users/${userId}/is-followed`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to check if user is followed');
        }
        const data = await response.json();
        return data;
    }

    static async updateUserProfile(data: { [key: string]: any }, token: string) {
        const response = await fetch(`${env.API_URL}/users/me`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            throw new Error('Failed to update user profile');
        }
        const updatedUser = await response.json();
        UserService.currentUser = User.fromAPI(updatedUser);
        return User.fromAPI(updatedUser);
    }

    static async changeProfilePicture(file: File, token: string) {
        const formData = new FormData();
        formData.append('file', file);
        const response = await fetch(`${env.API_URL}/users/profile-picture`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });
        if (!response.ok) {
            throw new Error('Failed to change profile picture');
        }
        return await response.json();
    }
}