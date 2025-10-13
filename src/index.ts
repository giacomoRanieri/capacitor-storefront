import { registerPlugin } from '@capacitor/core';

import type { CapacitorStorefrontPlugin } from './definitions';

const CapacitorStorefront = registerPlugin<CapacitorStorefrontPlugin>('CapacitorStorefront', {
  web: () => import('./web').then((m) => new m.CapacitorStorefrontWeb()),
});

export * from './definitions';
export { CapacitorStorefront };
