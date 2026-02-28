-- V2__bulk_data_1000.sql — 1000 simulation scenarios + results for repair-sim

-- ═══════ 100 SIMULATION SCENARIOS ═══════
INSERT INTO simulation_scenario (name, branch_count, avg_demands_per_day, simulation_days, junior_tech_ratio, techs_per_branch, inventory_strategy, city_tier, monthly_rent, avg_ticket_value, avg_parts_cost, status, created_at)
SELECT
    'Scenario ' || s || ' — ' ||
    (ARRAY['Conservative Growth','Aggressive Scaling','Steady State','High Demand',
           'Rural Expansion','Metro Saturation','Lean Ops','Premium Focus',
           'Budget Model','Franchise Pilot'])[1 + (s % 10)],
    1 + (s % 8),                     -- 1-8 branches
    3 + (s * 3 % 18),               -- 3-20 demands/day
    (ARRAY[30,60,90,180,365])[1 + (s % 5)],
    round((0.3 + (s % 6) * 0.1)::numeric, 2),
    2 + (s % 4),                     -- 2-5 techs
    (ARRAY['LOCAL','CENTRAL','HYBRID'])[1 + (s % 3)],
    1 + (s % 3),                     -- tier 1-3
    round((15000 + (s * 3500 % 135000))::numeric, 2),
    round((800 + (s * 130 % 2800))::numeric, 2),
    round((200 + (s * 40 % 1200))::numeric, 2),
    'COMPLETED',
    now() - ((s % 60)::text || ' days')::interval
FROM generate_series(1, 100) AS s;

-- ═══════ 1000 SIMULATION RESULTS (10 branch results per scenario) ═══════
INSERT INTO simulation_result (scenario_id, branch_index, total_revenue, total_parts_cost, total_labor_cost, total_rent, total_royalty, net_profit, profit_margin_percent, total_tickets, completed_tickets, sla_breach, first_time_fix_rate, mean_repair_time_hours, tech_utilization_percent, inventory_stockouts, break_even_tickets_per_day, created_at)
SELECT
    sc.id,
    (s2 % sc.branch_count),
    -- Revenue
    round((sc.avg_ticket_value * sc.avg_demands_per_day * sc.simulation_days * (0.8 + random() * 0.4))::numeric, 2),
    -- Parts cost (30-40% of revenue)
    round((sc.avg_parts_cost * sc.avg_demands_per_day * sc.simulation_days * (0.7 + random() * 0.6))::numeric, 2),
    -- Labor cost
    round((sc.techs_per_branch * 400 * sc.simulation_days * (0.6 + random() * 0.4))::numeric, 2),
    -- Rent
    round((sc.monthly_rent * (sc.simulation_days / 30.0))::numeric, 2),
    -- Royalty (7% of revenue)
    round((sc.avg_ticket_value * sc.avg_demands_per_day * sc.simulation_days * 0.07 * (0.8 + random() * 0.4))::numeric, 2),
    -- Net profit (calculated)
    round((sc.avg_ticket_value * sc.avg_demands_per_day * sc.simulation_days * (0.05 + random() * 0.20))::numeric, 2),
    round((5 + random() * 25)::numeric, 1),
    (sc.avg_demands_per_day * sc.simulation_days),
    round((sc.avg_demands_per_day * sc.simulation_days * (0.75 + random() * 0.20))::int),
    (random() * sc.avg_demands_per_day * sc.simulation_days * 0.08)::int,
    round((0.65 + random() * 0.30)::numeric, 2),
    round((1.5 + random() * 4.0)::numeric, 1),
    round((30 + random() * 60)::numeric, 1),
    (random() * sc.avg_demands_per_day * sc.simulation_days * 0.05)::int,
    round((sc.monthly_rent / sc.avg_ticket_value * 1.3 / 30.0 + 1)::numeric)::int,
    sc.created_at
FROM simulation_scenario sc
CROSS JOIN generate_series(1, 10) AS s2
WHERE sc.status = 'COMPLETED'
LIMIT 1000;
