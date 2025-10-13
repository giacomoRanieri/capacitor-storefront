export interface CapacitorStorefrontPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
