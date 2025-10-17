// wsClientSingleton.ts
import { useUserStore } from '@/store/user';

type MessageListener = (data: string) => void;

class WSClientSingleton {
    private static instance: WSClientSingleton;
    private ws: WebSocket | null = null;
    private listeners: MessageListener[] = [];
    private lockReconnect = false;
    private heartbeatInterval = 30000;
    private heartbeatTimer: ReturnType<typeof setInterval> | null = null;
    private url: string | null = null; // 动态 URL

    private constructor() {}

    /** 获取单例实例（必须传入 userId） */
    public static getInstance(userId: string | number): WSClientSingleton {
        if (!WSClientSingleton.instance) {
            WSClientSingleton.instance = new WSClientSingleton();
            WSClientSingleton.instance.url = `/api/websocket/${userId}`;
            WSClientSingleton.instance.connect();
        } else if (!WSClientSingleton.instance.url) {
            WSClientSingleton.instance.url = `/api/websocket/${userId}`;
            WSClientSingleton.instance.connect();
        }
        return WSClientSingleton.instance;
    }

    /** 初始化连接 */
    private connect(): void {
        if (!this.url) throw new Error('WebSocket URL 未设置');
        if (this.ws) return;

        this.ws = new WebSocket(this.url);

        this.ws.onopen = () => {
            console.log('WebSocket 已连接:', this.url);
            this.startHeartbeat();
        };

        this.ws.onmessage = (event: MessageEvent) => {
            this.listeners.forEach((fn) => fn(event.data));
        };

        this.ws.onclose = () => {
            console.log('WebSocket 已关闭，尝试重连...');
            this.reconnect();
        };

        this.ws.onerror = (err) => {
            console.error('WebSocket 错误', err);
            this.reconnect();
        };
    }

    /** 发送消息 */
    public send(data: string | object): void {
        if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
            console.warn('WebSocket 未连接，消息发送失败');
            return;
        }
        const payload = typeof data === 'string' ? data : JSON.stringify(data);
        this.ws.send(payload);
    }

    /** 添加消息监听 */
    public addListener(fn: MessageListener): void {
        this.listeners.push(fn);
    }

    /** 移除消息监听 */
    public removeListener(fn: MessageListener): void {
        this.listeners = this.listeners.filter((f) => f !== fn);
    }

    private reconnect(): void {
        if (this.lockReconnect) return;
        this.lockReconnect = true;

        setTimeout(() => {
            console.log('WebSocket 正在重连...');
            this.lockReconnect = false;
            this.ws = null;
            this.connect();
        }, 3000);
    }

    private startHeartbeat(): void {
        this.stopHeartbeat();
        this.heartbeatTimer = setInterval(() => {
            if (this.ws && this.ws.readyState === WebSocket.OPEN) {
                this.ws.send(JSON.stringify({ type: 'ping' }));
            }
        }, this.heartbeatInterval);
    }

    private stopHeartbeat(): void {
        if (this.heartbeatTimer) {
            clearInterval(this.heartbeatTimer);
            this.heartbeatTimer = null;
        }
    }

    public close(): void {
        this.stopHeartbeat();
        this.ws?.close();
        this.ws = null;
    }
}

// 封装一个全局方法获取实例
export function getWSClient(): WSClientSingleton {
    const userStore = useUserStore();
    const userId = userStore.loginUser?.id;
    return WSClientSingleton.getInstance(userId as number);
}
